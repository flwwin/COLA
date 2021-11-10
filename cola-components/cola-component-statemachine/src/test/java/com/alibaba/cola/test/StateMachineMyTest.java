package com.alibaba.cola.test;

import com.alibaba.cola.statemachine.Action;
import com.alibaba.cola.statemachine.Condition;
import com.alibaba.cola.statemachine.StateMachine;
import com.alibaba.cola.statemachine.StateMachineFactory;
import com.alibaba.cola.statemachine.builder.StateMachineBuilder;
import com.alibaba.cola.statemachine.builder.StateMachineBuilderFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * flw
 */
public class StateMachineMyTest {

    static String MACHINE_ID = "TestStateMachine";

    static enum States {
        WAITE, PASS, REJECT
    }

    static enum Events {
        AUDIT, AUDIT_REJECT, COMMIT, COMPLETE_INFORMATION
    }

    static class Context{
        String operator = "flw";
        String entityId =  "7758258";
    }

    @Test
    public void testExternalNormal(){
        StateMachineBuilder<States, Events, Context> builder = StateMachineBuilderFactory.create();
        builder.externalTransition()// 内部流转
                .from(States.WAITE)
                .to(States.PASS)
                .on(Events.COMPLETE_INFORMATION)
                .when(checkCondition())
                .perform(doAction());

        StateMachine<States, Events, Context> stateMachine = builder.build(MACHINE_ID);
        // 打印状态机里面的流程流转图谱
        stateMachine.showStateMachine();
        // 通过状态机执行 待审核状态执行审核操作，
        States target = stateMachine.fireEvent(States.WAITE, Events.AUDIT, new Context());
        Assert.assertEquals(States.WAITE, target);
    }
    @Test
    public void testInternalNormal(){
        StateMachineBuilder<States, Events, Context> builder = StateMachineBuilderFactory.create();
        builder.internalTransition()
            .within(States.WAITE)
            .on(Events.AUDIT)
            .when(checkCondition())
            .perform(doAction());

        StateMachine<States, Events, Context> stateMachine = builder.build(MACHINE_ID);
        // 打印状态机里面的流程流转图谱
        stateMachine.showStateMachine();
        // 通过状态机执行 待审核状态执行审核操作，
        States target = stateMachine.fireEvent(States.WAITE, Events.AUDIT, new Context());
        Assert.assertEquals(States.PASS, target);
    }

    private Condition<StateMachineMyTest.Context> checkCondition() {
        return (ctx) -> {return true;};
    }

    private Action<StateMachineMyTest.States, StateMachineMyTest.Events, StateMachineMyTest.Context> doAction() {
        return (from, to, event, ctx)->{
            System.out.println(ctx.operator+" is operating "+ctx.entityId+" from:"+from+" to:"+to+" on:"+event);
        };
    }
}
