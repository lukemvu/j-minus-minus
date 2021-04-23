// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a do-statement.
 */
public class JDoStatement extends JStatement {
    // Body.
    private JStatement body;

    // Test expression.
    private JExpression condition;

    // Break statement.
    public boolean hasBreak;
    public String breakLabel;

    // Continue statement.
    public boolean hasContinue;
    public String continueLabel;

    /**
     * Constructs an AST node for a do-statement.
     *
     * @param line      line in which the do-statement occurs in the source file.
     * @param body      the body.
     * @param condition test expression.
     */
    public JDoStatement(int line, JStatement body, JExpression condition) {
        super(line);
        this.body = body;
        this.condition = condition;
    }

    /**
     * {@inheritDoc}
     */
    public JStatement analyze(Context context) {
        JMember.enclosingStatement.push(this);
        body = (JStatement) body.analyze(context);
        condition = (JExpression) condition.analyze(context);
        condition.type().mustMatchExpected(line(), Type.BOOLEAN);
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
        if (hasContinue) {
            continueLabel = output.createLabel();
        }
        String bodyLabel = output.createLabel();
        if (hasContinue) {
            output.addLabel(continueLabel);
        }
        output.addLabel(bodyLabel);
        body.codegen(output);
        condition.codegen(output, bodyLabel, true);
        if (hasBreak) {
            output.addLabel(breakLabel);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JDoStatement:" + line, e);
        JSONElement e1 = new JSONElement();
        e.addChild("Body", e1);
        body.toJSON(e1);
        JSONElement e2 = new JSONElement();
        e.addChild("Condition", e2);
        condition.toJSON(e2);
    }
}
