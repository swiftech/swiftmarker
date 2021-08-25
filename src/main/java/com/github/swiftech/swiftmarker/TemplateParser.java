package com.github.swiftech.swiftmarker;

import com.github.swiftech.swiftmarker.template.*;
import com.github.swiftech.swstate.StateBuilder;
import com.github.swiftech.swstate.StateMachine;

import java.util.LinkedList;
import java.util.List;

import static com.github.swiftech.swiftmarker.constant.Constants.*;

/**
 * @author swiftech
 * @since 3.0
 */
public class TemplateParser {

    private final Logger log = Logger.getInstance();

    private final StateMachine<String, Character> sm;

    /**
     * Store parse result of the template.
     */
    private final List<Directive> parseResult = new LinkedList<>();

    /**
     * Cache to compose expressions and stanzas
     */
    private StringBuilder stanzaBuf = new StringBuilder();
    private StringBuilder expressionBuf = new StringBuilder();

    public TemplateParser() {
        StateBuilder<String, Character> builder = new StateBuilder<>();
        builder.state(S_PENDING_LOGIC)
                .in(payload -> {
                    log.trace("in pending logic: " + payload);
                    appendToStanza(payload); // may be not directive, so add to stanza first, if it isn't, this char will not be flushed.
                })
                .state(S_PENDING_OTHER)
                .in(payload -> {
                    log.trace("in pending other: " + payload);
                    appendToStanza(payload); // may be not directive, so add to stanza first, if it isn't, this char will not be flushed.
                })
                .state(S_IN_LOGIC)
                .in(payload -> {
                    log.trace("in logic: " + payload);
                    pushStanzaWithoutLatest();
                })
                .state(S_IN_LOOP)
                .in(payload -> {
                    log.trace("in loop: " + payload);
                    pushStanzaWithoutLatest();
                })
                .state(S_IN_VAR)
                .in(payload -> {
                    log.trace("in var: " + payload);
                    pushStanzaWithoutLatest();
                })
                .state(S_IN_EXP_LOGIC)
                .in(payload -> {
                    log.trace("in exp logic: " + payload);
                    appendToExpression(payload);
                })
                .state(S_IN_EXP_LOOP)
                .in(payload -> {
                    log.trace("in exp loop: " + payload);
                    appendToExpression(payload);
                })
                .state(S_IN_EXP_VAR)
                .in(payload -> {
                    log.trace("in exp var: " + payload);
                    appendToExpression(payload);
                })
                .state(S_ESCAPING)
                .in(payload -> {
                    log.trace("escaping: " + payload);
                })
                .state(S_IN_STANZA)
                .in(payload -> {
                    log.trace("in stanza: " + payload);
                })
                .initialize("ready", S_READY)
                .action("?", S_READY, S_PENDING_LOGIC)
                .action("$", S_READY, S_PENDING_OTHER)
                .action("stanza", S_READY, S_IN_STANZA)
                .action("??", S_PENDING_LOGIC, S_PENDING_LOGIC)
                .action("?$", S_PENDING_LOGIC, S_PENDING_OTHER)
                .action("?{", S_PENDING_LOGIC, S_IN_LOGIC)
                .action("?\\{", S_PENDING_LOGIC, S_ESCAPING)
                .action("?*", S_PENDING_LOGIC, S_IN_STANZA)
                .action("$$", S_PENDING_OTHER, S_PENDING_OTHER)
                .action("$?", S_PENDING_OTHER, S_PENDING_LOGIC)
                .action("$[", S_PENDING_OTHER, S_IN_LOOP)
                .action("${", S_PENDING_OTHER, S_IN_VAR)
                .action("$\\{ or $\\[", S_PENDING_OTHER, S_ESCAPING)
                .action("$*", S_PENDING_OTHER, S_IN_STANZA)
                .action("?\\{* or $\\{* or $\\[*", S_ESCAPING, S_IN_STANZA)
                .action("?{*", S_IN_LOGIC, S_IN_EXP_LOGIC)
                .action("$[*", S_IN_LOOP, S_IN_EXP_LOOP)
                .action("${*", S_IN_VAR, S_IN_EXP_VAR)
                .action("?{*", S_IN_EXP_LOGIC, S_IN_STANZA)
                .action("?{**", S_IN_EXP_LOGIC, S_IN_EXP_LOGIC)
                .action("$[*]", S_IN_EXP_LOOP, S_IN_STANZA)
                .action("$[**", S_IN_EXP_LOOP, S_IN_EXP_LOOP)
                .action("${*}", S_IN_EXP_VAR, S_IN_STANZA)
                .action("${**", S_IN_EXP_VAR, S_IN_EXP_VAR)
                .action("?{}", S_IN_LOGIC, S_IN_STANZA)
                .action("$[]", S_IN_LOOP, S_IN_STANZA)
                .action("${}", S_IN_VAR, S_IN_STANZA) // invalid directive, ignore
                .action("**", S_IN_STANZA, S_IN_STANZA)
                .action("?", S_IN_STANZA, S_PENDING_LOGIC)
                .action("$", S_IN_STANZA, S_PENDING_OTHER)
        ;
        this.sm = new StateMachine<>(builder);
    }

    public List<Directive> parse(String id, String template) {
        this.sm.start(id);
        template.chars().forEach(c -> {
            log.trace(String.valueOf((char) c));
            if (c == '$') {
                this.sm.post(id, S_PENDING_OTHER, (char) c);
            }
            else if (c == '?') {
                this.sm.post(id, S_PENDING_LOGIC, (char) c);
            }
            else if (c == '\\'){
                this.sm.post(id, S_ESCAPING, (char)c);
            }
            else if (c == '[') {
                if (sm.isState(id, S_PENDING_OTHER)) {
                    sm.post(id, S_IN_LOOP, (char) c);
                }
                else {
                    appendToStanza((char) c);
                    sm.post(id, S_IN_STANZA, (char) c);
                }
            }
            else if (c == '{') {
                if (sm.isState(id, S_PENDING_OTHER)) {
                    sm.post(id, S_IN_VAR, (char) c);
                }
                else if (sm.isState(id, S_PENDING_LOGIC)) {
                    sm.post(id, S_IN_LOGIC, (char) c);
                }
                else {
                    appendToStanza((char) c);
                    sm.post(id, S_IN_STANZA, (char) c);
                }
            }
            else if (c == '}') {
                if (sm.isStateIn(id, S_IN_LOGIC, S_IN_VAR)) {
                    pushExpression(id);
                    sm.post(id, S_IN_STANZA, (char) c);
                }
                else if (sm.isStateIn(id, S_IN_EXP_LOGIC)) {
                    pushExpression(id);
                    sm.post(id, S_IN_STANZA, (char) c);
                }
                else if (sm.isStateIn(id, S_IN_EXP_VAR)) {
                    pushExpression(id);
                    sm.post(id, S_IN_STANZA, (char) c);
                }
                else {
                    appendToStanza((char) c);
                    sm.post(id, S_IN_STANZA, (char) c);
                }
            }
            else if (c == ']') {
                if (sm.isStateIn(id, S_IN_LOOP)) {
                    pushExpression(id);
                    sm.post(id, S_IN_STANZA, (char) c);
                }
                else if (sm.isState(id, S_IN_EXP_LOOP)) {
                    pushExpression(id);
                    sm.post(id, S_IN_STANZA, (char) c);
                }
                else {
                    appendToStanza((char) c);
                    sm.post(id, S_IN_STANZA, (char) c);
                }
            }
            else {
                if (sm.isStateIn(id, S_IN_LOGIC)) {
                    sm.post(id, S_IN_EXP_LOGIC, (char) c);
                }
                else if (sm.isState(id, S_IN_LOOP)) {
                    sm.post(id, S_IN_EXP_LOOP, (char) c);
                }
                else if (sm.isState(id, S_IN_VAR)) {
                    sm.post(id, S_IN_EXP_VAR, (char) c);
                }
                else if (sm.isStateIn(id, S_PENDING_LOGIC, S_PENDING_OTHER, S_ESCAPING)) {
                    appendToStanza((char) c);
                    sm.post(id, S_IN_STANZA, (char) c);
                }
                else {
                    if (sm.isStateIn(id, S_IN_EXP_LOGIC, S_IN_EXP_LOOP, S_IN_EXP_VAR)) {
                        // still in expression
                        sm.post(id, sm.getCurrentState(id), (char) c);
                    }
                    else {
                        // still in stanza
                        appendToStanza((char) c);
                        sm.post(id, S_IN_STANZA, (char) c);
                    }
                }
            }
        });
        pushStanza();
        log.debug("Show tailed parse results: ");
        for (int i = 0; i < parseResult.size(); i++) {
            Directive directive = parseResult.get(i);
            log.debug(String.format("[%2d] %s", i, directive));
        }
        return parseResult;
    }


    private void appendToStanza(char c) {
        stanzaBuf.append(c);
    }

    private void appendToExpression(char c) {
        expressionBuf.append(c);
    }

    private void pushStanza() {
        if (stanzaBuf.length() > 0) {
            // 如果一个可嵌套指令单独一行的，也就是前后各有一个换行符，那么删除一个换行符（这个只处理常规的情况，对于模版结尾出现可嵌套指令的情况，只能在最后特殊处理）
            System.out.printf("Stanza: '%s'%n", stanzaBuf);
            Stanza newStanza = new Stanza(stanzaBuf.toString());
            if (parseResult.size() > 0) {
                Directive preDirective = parseResult.get(parseResult.size() - 1);
                preDirective.setNext(newStanza); // set the next directive
                newStanza.setPrevious(preDirective);// set the previous directive
            }
            parseResult.add(newStanza);
            stanzaBuf = new StringBuilder();
        }
    }

    private void pushStanzaWithoutLatest() {
        stanzaBuf.deleteCharAt(stanzaBuf.length() - 1);
        pushStanza();
    }

    private void pushExpression(String id) {
        Directive directive = null;
        if (expressionBuf.length() > 0) {
            System.out.println("Expression: " + expressionBuf);
            if (sm.isState(id, S_IN_EXP_LOGIC)) {
                directive = new LogicBegin(expressionBuf.toString());
            }
            else if (sm.isState(id, S_IN_EXP_LOOP)) {
                directive = new LoopBegin(expressionBuf.toString());
            }
            else {
                directive = new Var(expressionBuf.toString());
            }
        }
        else {
            if (sm.isState(id, S_IN_LOGIC)) {
                directive = new LogicEnd();
            }
            else if (sm.isState(id, S_IN_LOOP)) {
                directive = new LoopEnd();
            }
        }
        if (directive != null) {
            if (parseResult.size() > 0) {
                Directive preDirective = parseResult.get(parseResult.size() - 1);
                preDirective.setNext(directive); // set the next directive
                directive.setPrevious(preDirective);// set the previous directive
            }
            parseResult.add(directive);
            expressionBuf = new StringBuilder();
        }
    }

    public List<Directive> getParseResult() {
        return parseResult;
    }

}
