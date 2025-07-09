# Camunda 7 to 8 Migration Example

This project contains a simple end-to-end migration example to get a Camunda 7 solution to Camunda 8. It is deliberatily simple enough to show migration end-to-end, but therefore the complexity is limited.

We know, that most real-life projects are more complex to migrate, still we want to use this as a baseline to discuss the approach and showcase migration tooling.

The [Migration Journey](https://docs.camunda.io/docs/next/guides/migrating-from-camunda-7/migration-journey/) in our migration guide touches on more details.

## The Camunda 7 Process Solution

![The sample process](process.png)

The process:
- Has a Service Task with Java Delegate Expression: `#{sampleJavaDelegate}`
- Has a Service Task with Expression: `#{sampleBean.someMethod(y)}`
- Calls a sub process
- Uses an XOR gateway with JUEL expressions on the outgoing flows: `#{x>5}` / `#{x<=5}`
- Has a User Task with an assignment to the user `demo`
- Has a timer event with a duration of 5 minutes: `PT5M`

The subprocess is deliberatley simple:
![The sub process](sub-process.png)

Code-wise it is a simple Spring Boot application:

```java
@SpringBootApplication
public class Application {
  public static void main(String... args) {
  }
}
```

Here the delegate and Spring bean:

```java
@Component
public class SampleJavaDelegate implements JavaDelegate {
  public void execute(DelegateExecution execution) throws Exception {
    Object x = execution.getVariable("x");
	System.out.println("SampleJavaDelegate " + x);
    execution.setVariable("y", "hello world");
  }
}
@Component
public class SampleBean {
  public int someMethod(String text) {
    System.out.println("SampleBean.someMethod('" + text + "')");
    return 42;
  }
}
```

And of course this project contains a JUnit test case:

```java

@SpringBootTest
public class ApplicationTest {
	
  @Test
  void pathWithUserTask() {
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(
            "sample-process-solution-process",
            Variables.createVariables().putValue("x", 7));
    // assert / verify that we arrive in the user task with the name "Say hello to demo"
    assertThat(processInstance).isWaitingAt(findId("Say hello to demo"));
    assertThat(task())
    	.hasName("Say hello to demo")
    	.isAssignedTo("demo");
    
    // complete that task, so that the process instance advances
    complete(task());
    // Assert that it completed in the right end event, and that a Spring Bean hooked into the service task has written the expected process variable
    assertThat(processInstance).isEnded().hasPassed("Event_GreaterThan5");
    assertThat(processInstance).variables().containsEntry("theAnswer", 42);
  }

  @Test
  void pathWithTimer() {
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(
                "sample-process-solution-process", //
                Variables.createVariables().putValue("x", 5));
    
    // Query and trigger timmer
    // Execute the pending job (e.g. a timer or async)
    Job timerJob = managementService().createJobQuery()
      .processInstanceId(processInstance.getId())
      .singleResult();
    managementService().executeJob(timerJob.getId());
    
    assertThat(processInstance).isEnded().hasPassed("Event_SmallerThan5");
  }
```

And finally we simulate some load in the system by just starting some process instances on startup:

```java
  @PostConstruct
  public void startSomeProcesses() {
    System.out.println("Let's start some processes to have data in the system...");

    Random random = new Random();
    for (int i = 0; i < 100; i++) {
      int rand = random.nextInt(10); // 0–9

      VariableMap variables = Variables.createVariables();
      variables.putValue("x", rand);

      ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("sample-process-solution-process",
          variables);
      System.out.println("Started " + processInstance.getId());
    }
  }
```

## The Migration Process

Let's mih



## The Camunda 8 Process Solution