package com.github.swiftech.swiftmarker;

import com.github.swiftech.swiftmarker.template.Directive;
import com.github.swiftech.swiftmarker.template.LogicBegin;
import com.github.swiftech.swiftmarker.template.LoopBegin;
import com.github.swiftech.swiftmarker.template.NestableDirective;

import java.util.Stack;

/**
 * @author swiftech
 * @since 3.0.1
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
        if (directiveStack.isEmpty()) {
            return false;
        }
        Directive directive = directiveStack.peek();
        return directive instanceof LogicBegin;
    }

    public boolean isTopLoopBegin() {
        if (directiveStack.isEmpty()) {
            return false;
        }
        Directive directive = directiveStack.peek();
        return directive instanceof LoopBegin;
    }

    public boolean isTopAvailable() {
        if (!directiveStack.isEmpty()) {
            Directive lastDirective = directiveStack.peek();
            if (lastDirective instanceof NestableDirective) {
                return ((NestableDirective) lastDirective).isAvailable();
            }
        }
        return true;
    }


    public void printStack() {
        directiveStack.forEach(directive -> System.out.printf("%s(%s),", directive.getClass().getSimpleName(), (directive instanceof NestableDirective ? ((NestableDirective) directive).isAvailable() : "")));
        System.out.println();
    }

    public Stack<Directive> getStack() {
        return directiveStack;
    }
}
