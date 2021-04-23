// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a for-statement.
 */
class JForStatement extends JStatement {
    // Initialization.
    private ArrayList<JStatement> init;

    // Test expression
    private JExpression condition;

    // Update.
    private ArrayList<JStatement> update;

    // The body.
    private JStatement body;

    // Local Context
    private LocalContext context;

    // Break statement.
    public boolean hasBreak;
    public String breakLabel;

    /**
     * Constructs an AST node for a for-statement.
     *
     * @param line      line in which the for-statement occurs in the source file.
     * @param init      the initialization.
     * @param condition the test expression.
     * @param update    the update.
     * @param body      the body.
     */
    public JForStatement(int line, ArrayList<JStatement> init, JExpression condition,
                         ArrayList<JStatement> update, JStatement body) {
        super(line);
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }

    /**
     * {@inheritDoc}
     */
    public JForStatement analyze(Context context) {

        JMember.enclosingStatement.push(this);

        // Create new LocalContext with context as the parent
        this.context = new LocalContext(context);

        // Analyze the init in the new context
        if (init != null) {
            for (int i = 0; i < init.size(); i++) {
                init.set(i, (JStatement) init.get(i).analyze(this.context));
            }
        }

        // Analyze the condition in the new context and type check for boolean
        if (condition != null) {
            condition = (JExpression) condition.analyze(this.context);
            condition.type().mustMatchExpected(line(), Type.BOOLEAN);
        }

        // Analyze the update in the new context
        if (update != null) {
            for (int i = 0; i < update.size(); i++) {
                update.set(i, (JStatement) update.get(i).analyze(this.context));
            }
        }

        // Analyze body in the new context
        body = (JStatement) body.analyze(this.context);

        JMember.enclosingStatement.pop();

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        if (hasBreak) {
            breakLabel = output.createLabel();
        }
        String bodyLabel = output.createLabel();
        String endLabel = output.createLabel();
        if (init != null) {
            for (JStatement statement : init) {
                statement.codegen(output);
            }
        }
        output.addLabel(bodyLabel);
        if (condition != null) {
            condition.codegen(output, endLabel, false);
        }
        body.codegen(output);
        if (update != null) {
            for (JStatement statement : update) {
                statement.codegen(output);
            }
        }
        output.addBranchInstruction(GOTO, bodyLabel);
        output.addLabel(endLabel);
        if (hasBreak) {
            output.addLabel(breakLabel);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JForStatement:" + line, e);
        if (init != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Init", e1);
            for (JStatement stmt : init) {
                stmt.toJSON(e1);
            }
        }
        if (condition != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Condition", e1);
            condition.toJSON(e1);
        }
        if (update != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Update", e1);
            for (JStatement stmt : update) {
                stmt.toJSON(e1);
            }
        }
        if (body != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Body", e1);
            body.toJSON(e1);
        }
    }
}
