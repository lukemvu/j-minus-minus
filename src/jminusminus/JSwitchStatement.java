// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;
import java.util.TreeMap;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a switch-statement.
 */
public class JSwitchStatement extends JStatement {
    // Test expression.
    private JExpression condition;

    // List of switch-statement groups.
    private ArrayList<SwitchStatementGroup> stmtGroup;

    // extra stuff
    private LocalContext context;

    private int hi, lo, nLabels;
    private ArrayList<String> labels;
    private TreeMap<Integer, String> matchLabelPairs;

    /**
     * Constructs an AST node for a switch-statement.
     *
     * @param line      line in which the switch-statement occurs in the source file.
     * @param condition test expression.
     * @param stmtGroup list of statement groups.
     */
    public JSwitchStatement(int line, JExpression condition,
                            ArrayList<SwitchStatementGroup> stmtGroup) {
        super(line);
        this.condition = condition;
        this.stmtGroup = stmtGroup;
    }

    /**
     * {@inheritDoc}
     */
    public JStatement analyze(Context context) {
        // Deciding switch instruction
        hi = Integer.MIN_VALUE;
        lo = Integer.MAX_VALUE;
        nLabels = 0;
        labels = new ArrayList<String>();
        matchLabelPairs = new TreeMap<Integer, String>();

        // Analyzing the condition to ensure it is an integer.
        condition = (JExpression) condition.analyze(context);
        condition.type().mustMatchExpected(line(), Type.INT);

        // Analyzing the case expressions and making sure they are integer literals.
        for (SwitchStatementGroup swtchBlkStmtGroup : stmtGroup) {
            //JExpression switchLabel : swtchBlkStmtGroup.getSwitchLabels()
            for (int i = 0; i<swtchBlkStmtGroup.getSwitchLabels().size(); i++) {
                JExpression switchLabel = swtchBlkStmtGroup.getSwitchLabels().get(i);
                if (switchLabel instanceof JLiteralInt) {
                    swtchBlkStmtGroup.getSwitchLabels().set(
                            i, (JExpression) switchLabel.analyze(context)
                    );
                    int val = ((JLiteralInt) switchLabel).toInt();
                    if (val > hi) hi = val;
                    if (val < lo) lo = val;
                    nLabels++;
                    String labelName = "Case" + val;
                    labels.add(labelName);
                    matchLabelPairs.put(val, labelName);
                }
            }
        }

        // Analyzing statements in each case group in the new context.
        for (SwitchStatementGroup swtchBlkStmtGroup : stmtGroup) {
            this.context = new LocalContext(context);
            for (int i = 0; i < swtchBlkStmtGroup.getBlock().size(); i++) {
                swtchBlkStmtGroup.getBlock().set(
                        i, (JStatement) swtchBlkStmtGroup.getBlock().get(i).analyze(this.context)
                );
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(ILOAD_1);

        // Deciding switch instruction
        long tableSpaceCost = 5 + hi - lo;
        long tableTimeCost = 3;
        long lookupSpaceCost = 3 + 2 * nLabels;
        long lookupTimeCost = nLabels;
        int opcode = nLabels > 0 && (tableSpaceCost + 3 * tableTimeCost
                <= lookupSpaceCost + 3 * lookupTimeCost) ? TABLESWITCH : LOOKUPSWITCH;

        if (opcode == TABLESWITCH) {
            output.addTABLESWITCHInstruction("Default", lo, hi, labels);
        } else {
            output.addLOOKUPSWITCHInstruction("Default", nLabels, matchLabelPairs);
        }

        for (SwitchStatementGroup swtchBlkStmtGroup : stmtGroup) {
            for (JExpression switchLabel : swtchBlkStmtGroup.getSwitchLabels()) {
                // Adding case labels
                if (switchLabel != null) {
                    int val = ((JLiteralInt) switchLabel).toInt();
                    output.addLabel("Case" + val);
                } else {
                    output.addLabel("Default");
                }
            }
            for (JStatement statement : swtchBlkStmtGroup.getBlock()) {
                // Generating code per statement
                statement.codegen(output);
            }
        }
        output.addLabel("EndSwitch");
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JSwitchStatement:" + line, e);
        JSONElement e1 = new JSONElement();
        e.addChild("Condition", e1);
        condition.toJSON(e1);
        for (SwitchStatementGroup group : stmtGroup) {
            group.toJSON(e);
        }
    }
}

/**
 * A switch statement group consists of case labels and a block of statements.
 */
class SwitchStatementGroup {
    // Case labels.
    private ArrayList<JExpression> switchLabels;

    // Block of statements.
    private ArrayList<JStatement> block;

    /**
     * Constructs a switch-statement group.
     *
     * @param switchLabels case labels.
     * @param block        block of statements.
     */
    public SwitchStatementGroup(ArrayList<JExpression> switchLabels, ArrayList<JStatement> block) {
        this.switchLabels = switchLabels;
        this.block = block;
    }

    public ArrayList<JExpression> getSwitchLabels() {
        return switchLabels;
    }

    public ArrayList<JStatement> getBlock() {
        return block;
    }

    /**
     * Stores information about this switch statement group in JSON format.
     *
     * @param json the JSON emitter.
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("SwitchStatementGroup", e);
        for (JExpression label : switchLabels) {
            JSONElement e1 = new JSONElement();
            if (label != null) {
                e.addChild("Case", e1);
                label.toJSON(e1);
            } else {
                e.addChild("Default", e1);
            }
        }
        if (block != null) {
            for (JStatement stmt : block) {
                stmt.toJSON(e);
            }
        }
    }
}
