package TrackingNodes;

import CommunicationClasses.RobotDataReader;
import WeldProgramNodes.WeldOFFService;
import WeldProgramNodes.WeldONService;
import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.program.CreationContext;
import com.ur.urcap.api.contribution.program.CreationContext.NodeCreationType;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.domain.ProgramAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.program.ProgramModel;
import com.ur.urcap.api.domain.program.nodes.ProgramNodeFactory;
import com.ur.urcap.api.domain.program.nodes.builtin.MoveNode;
import com.ur.urcap.api.domain.program.nodes.builtin.WaypointNode;
import com.ur.urcap.api.domain.program.nodes.builtin.configurations.waypointnode.BlendParameters;
import com.ur.urcap.api.domain.program.nodes.builtin.configurations.waypointnode.WaypointMotionParameters;
import com.ur.urcap.api.domain.program.nodes.builtin.configurations.waypointnode.WaypointNodeConfig;
import com.ur.urcap.api.domain.program.nodes.builtin.configurations.waypointnode.WaypointNodeConfigFactory;
import com.ur.urcap.api.domain.program.structure.TreeNode;
import com.ur.urcap.api.domain.program.structure.TreeStructureException;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.undoredo.UndoRedoManager;
import com.ur.urcap.api.domain.undoredo.UndoableChanges;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardNumberInput;
import com.ur.urcap.api.domain.validation.ErrorHandler;
import com.ur.urcap.api.domain.value.Pose;
import com.ur.urcap.api.domain.value.ValueFactoryProvider;
import com.ur.urcap.api.domain.value.jointposition.JointPosition;
import com.ur.urcap.api.domain.value.jointposition.JointPositionFactory;
import com.ur.urcap.api.domain.value.jointposition.JointPositions;
import com.ur.urcap.api.domain.value.simple.Angle;
import com.ur.urcap.api.domain.value.simple.Length;
import com.ur.urcap.api.domain.value.simple.Speed;
import styleClasses.TemplateType;
import java.awt.*;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;
import com.ur.urcap.api.domain.validation.ErrorHandler;
import com.ur.urcap.api.domain.value.Pose;
import com.ur.urcap.api.domain.value.PoseFactory;
import com.ur.urcap.api.domain.value.ValueFactoryProvider;
import com.ur.urcap.api.domain.value.expression.ExpressionBuilder;
import com.ur.urcap.api.domain.value.expression.InvalidExpressionException;
import com.ur.urcap.api.domain.value.jointposition.JointPosition;
import com.ur.urcap.api.domain.value.jointposition.JointPositionFactory;
import com.ur.urcap.api.domain.value.jointposition.JointPositions;
import com.ur.urcap.api.domain.value.simple.Acceleration;
import com.ur.urcap.api.domain.value.simple.Angle;
import com.ur.urcap.api.domain.value.simple.Length;
import com.ur.urcap.api.domain.value.simple.Speed;
import com.ur.urcap.api.domain.variable.GlobalVariable;
import com.ur.urcap.api.domain.variable.Variable;
import com.ur.urcap.api.domain.variable.VariableException;
import com.ur.urcap.api.domain.variable.VariableFactory;
import org.graalvm.compiler.nodes.calc.AddNode;
import styleClasses.TemplateType;
import com.ur.urcap.api.contribution.program.CreationContext.NodeCreationType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TrackingContribution implements ProgramNodeContribution {

    private static final String TEMPLATE_KEY = "templateType";
    private static final String VARIABLE_TARGET_KEY = "variable";
    private ProgramAPIProvider apiProvider;
    private TrackingView view;
    private DataModel model;
    private Timer uiTimer;
    private int flag;
    private ProgramNodeFactory programNodeFactory;
    private WaypointNodeConfigFactory waypointNodeConfigFactory;
    private JointPositionFactory jointPositionFactory;
    private ValueFactoryProvider valueFactoryProvider;
    private UndoRedoManager undoRedoManager;
    private double speed;
    private static final String INPUT_KEY = "input_key";
    private static final String INPUT_DEFAULT_VALUE = "not set";
    private KeyboardInputFactory keyboardInputFactory;
    int counter = 0;

    public TrackingContribution(ProgramAPIProvider apiProvider, TrackingView view, DataModel model, CreationContext context) {
        this.apiProvider = apiProvider;
        this.view = view;
        this.model = model;
        lockChildSequence(apiProvider);
        if (context.getNodeCreationType() == NodeCreationType.NEW) {
            this.setModel(TemplateType.EMPTY.getName());
        }
        undoRedoManager = apiProvider.getProgramAPI().getUndoRedoManager();
        keyboardInputFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getKeyboardInputFactory();
        programNodeFactory = apiProvider.getProgramAPI().getProgramModel().getProgramNodeFactory();
        waypointNodeConfigFactory = programNodeFactory.createWaypointNode().getConfigFactory();
        valueFactoryProvider = apiProvider.getProgramAPI().getValueFactoryProvider();
        jointPositionFactory = valueFactoryProvider.getJointPositionFactory();
    }

    RobotDataReader robotDataReader = new RobotDataReader();

    @Override
    public void openView() {
        update();
        uiTimer = new Timer(true);
        uiTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        updateData();
                    }
                });
            }
        }, 0, 1000);
    }

    @Override
    public void closeView() {
        if (uiTimer != null) {
            uiTimer.cancel();
        }
    }

    @Override
    public String getTitle() {
        return "Tracking Weld Mode";
    }

    @Override
    public boolean isDefined() {
        return true;
    }

    @Override
    public void generateScript(ScriptWriter writer) {
        writer.writeChildren();
    }

    private void update(){
        view.setTextField(model.get(INPUT_KEY, INPUT_DEFAULT_VALUE));
    }

    public KeyboardNumberInput getKeyboardForInput() {
        KeyboardNumberInput keyboard = keyboardInputFactory.createDoubleKeypadInput();
        return keyboard;
    }

    public KeyboardInputCallback<Double> getKeyBoardCallBack() {
        return new KeyboardInputCallback<Double>() {
            @Override
            public void onOk(Double value) {
                final Double inputText = value;
                undoRedoManager.recordChanges(new UndoableChanges() {
                    @Override
                    public void executeChanges() {
                        model.set(INPUT_KEY, inputText);
                    }
                });
                view.setTextField(value.toString());
            }
        };
    }

    private void setModel(final String variable) {
        model.set(TEMPLATE_KEY, variable);
    }

    private void updateData(){
        robotDataReader.readNow();
        try{
            Integer[] in = robotDataReader.getDigitalIn();
            AddNodesIN(in);
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("error");
        }

    }

    private String getSpeed(){
        return model.get(INPUT_KEY, INPUT_DEFAULT_VALUE);
    }

    public void createArcOn() {
        apiProvider.getProgramAPI().getUndoRedoManager().recordChanges(new UndoableChanges() {
            @Override
            public void executeChanges() {
            setModel(TemplateType.ARCON.getName());
            createSubtree(TemplateType.ARCON);
            //view.update(TrackingContribution.this);
            }
        });
    }

    public void createArcOff() {
        apiProvider.getProgramAPI().getUndoRedoManager().recordChanges(new UndoableChanges() {
            @Override
            public void executeChanges() {
            setModel(TemplateType.ARCOFF.getName());
            createSubtree(TemplateType.ARCOFF);
            //view.update(TrackingContribution.this);
            }
        });
    }


    public void reset(){
        apiProvider.getProgramAPI().getUndoRedoManager().recordChanges(new UndoableChanges() {
            @Override
            public void executeChanges() {
            setModel(TemplateType.EMPTY.getName());
            clearSubtree();
            //view.update(TrackingContribution.this);
            counter = 0;
            }
        });
    }

    private void clearSubtree() {
        ProgramAPI programAPI = apiProvider.getProgramAPI();
        ProgramModel programModel = programAPI.getProgramModel();

        TreeNode subTree = programModel.getRootTreeNode(this);

        try {
            for (TreeNode child : subTree.getChildren()) {
                subTree.removeChild(child);
            }
        } catch (TreeStructureException e) {
            e.printStackTrace();
            // See e.getMessage() for explanation
        }
    }

    private void lockChildSequence(ProgramAPIProvider apiProvider) {
        ProgramAPI programAPI = apiProvider.getProgramAPI();
        ProgramModel programModel = programAPI.getProgramModel();
        TreeNode root = programModel.getRootTreeNode(this);
        root.setChildSequenceLocked(true);
    }

    public TemplateType getTemplateType() {
        String templateTypeName = model.get(TEMPLATE_KEY, TemplateType.EMPTY.getName());
        return TemplateType.valueOfByName(templateTypeName);
    }

    private void createSubtree(TemplateType template) {
        ProgramAPI programAPI = apiProvider.getProgramAPI();
        ProgramModel programModel = programAPI.getProgramModel();
        ProgramNodeFactory nf = programModel.getProgramNodeFactory();
        TreeNode root = programModel.getRootTreeNode(this);
        try {
            addNode(template, root, nf);
        } catch (TreeStructureException e) {
            e.printStackTrace();
        }
    }

    private void addNode(TemplateType template, TreeNode root, ProgramNodeFactory nf) throws TreeStructureException {
        switch (template) {
            case ARCON:
                root.addChild(nf.createURCapProgramNode(WeldONService.class));
                break;
            case ARCOFF:
                root.addChild(nf.createURCapProgramNode(WeldOFFService.class));
                break;
            default:
                break;
        }
    }

    private void AddNodesIN(Integer[] inputs_array){
        if(inputs_array[5] == 1){
            createArcOn();
        }
        else if(inputs_array[6] == 1){
            createArcOff();
        }
        if(inputs_array[4] == 1){
            addWaypoint();
        }
    }

    private void addWaypoint(){
        final ProgramAPI programAPI = apiProvider.getProgramAPI();
        ProgramModel programModel = programAPI.getProgramModel();
        final ProgramNodeFactory nf = programModel.getProgramNodeFactory();
        final ValueFactoryProvider valueFactoryProvider = apiProvider.getProgramAPI().getValueFactoryProvider();
        final TreeNode root = programModel.getRootTreeNode(this);
        robotDataReader.readNow();
        double[] tcp_pose = robotDataReader.getActualTcpPose();
        double[] tcp_jointPositions = robotDataReader.getActualJointPose();
        final Pose pose = valueFactoryProvider.getPoseFactory().createPose(tcp_pose[0], tcp_pose[1], tcp_pose[2], tcp_pose[3], tcp_pose[4], tcp_pose[5], Length.Unit.M, Angle.Unit.RAD);
        final JointPositions jointPositions = valueFactoryProvider.getJointPositionFactory().createJointPositions(tcp_jointPositions[0], tcp_jointPositions[1], tcp_jointPositions[2],
                tcp_jointPositions[3], tcp_jointPositions[4], tcp_jointPositions[5], Angle.Unit.RAD);
        JointPosition[] jointPosition = jointPositions.getAllJointPositions();
        undoRedoManager.recordChanges(new UndoableChanges() {
            @Override
            public void executeChanges() {
            try {
                MoveNode targetMoveL = nf.createMoveNodeNoTemplate();
                Speed speed = valueFactoryProvider.getSimpleValueFactory().createSpeed(Double.parseDouble(getSpeed()), Speed.Unit.MM_S);
                targetMoveL.setConfig(targetMoveL.getConfigBuilders().createMoveLConfigBuilder().setToolSpeed(speed, ErrorHandler.AUTO_CORRECT).build());
                WaypointNode targetWaypoint = nf.createWaypointNode();
                BlendParameters targetWaypointDefaultBlends = targetWaypoint.getConfigFactory().createNoBlendParameters();
                WaypointMotionParameters targetMotionParams = targetWaypoint.getConfigFactory().createSharedMotionParameters();
                WaypointNodeConfig targetConfig = targetWaypoint.getConfigFactory().createFixedPositionConfig(pose, jointPositions, targetWaypointDefaultBlends, targetMotionParams);
                targetWaypoint.setConfig(targetConfig);
                TreeNode moveTreeNode = root.addChild(targetMoveL);
                moveTreeNode.addChild(targetWaypoint);
            } catch (TreeStructureException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            }
        });

    }
}
