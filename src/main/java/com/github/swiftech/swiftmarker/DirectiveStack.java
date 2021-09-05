package com.github.swiftech.swiftmarker;

import com.github.swiftech.swiftmarker.template.Directive;
import com.github.swiftech.swiftmarker.template.LogicBegin;
import com.github.swiftech.swiftmarker.template.LoopBegin;

import java.util.Stack;

/**
 * @author swiftech
 */
public class DirectiveStack {

    private final Stack<Directive> directiveStack = new Stack<>();

    public void push(Directive directive) {
        directiveStack.push(directive);
    }

    public Directive pop() {
        return directiveStack.pop();
    }

    public Directive peek() {
        return directiveStack.peek();
    }

    public boolean isTopLogicBegin() {
        Directive directive = directiveStack.peek();
        return directive instanceof LogicBegin;
    }

    public boolean isTopLoopBegin() {
        Directive directive = directiveStack.peek();
        return directive instanceof LoopBegin;
    }


}
