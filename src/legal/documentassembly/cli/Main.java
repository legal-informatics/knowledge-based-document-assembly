package legal.documentassembly.cli;

import java.util.ArrayList;
import java.util.Map;

import legal.documentassembly.ExerciseEngine;
import legal.documentassembly.ReasonerUtil;
import legal.documentassembly.RulebaseUtil;
import legal.documentassembly.TemplateUtil;
import legal.documentassembly.bean.Exercise;
import legal.documentassembly.bean.RuleFact;
import legal.documentassembly.bean.Step;
import legal.documentassembly.bean.TemplateFact;
import legal.documentassembly.cli.util.UiUtil;

public class Main {

	private static String default_exercise_filename = "exercise_art246.xml";
	
	public static void main(String[] args) {
		System.out.println("[1] Run exercise");
		System.out.println("[2] Create exercise");
		String choice = UiUtil.readValue("Choice");
		if ("1".equals(choice)) {
			String filename = UiUtil.readValue("Exercise filename [" + default_exercise_filename + "]");
			if ("".equals(filename))
				filename = default_exercise_filename;
			runExercise(filename);
		} else if ("2".equals(choice))
			createExercise();
	}
	
	private static void runExercise(String exercise_filename) {
		ExerciseEngine engine = new ExerciseEngine(exercise_filename);

		System.out.println("\n\nEnter the minus sign to return to the previous step\n");
		while (!engine.endOfExercise()) {
			Step step = engine.getCurrentStep();
			String defaultValue = " [" + step.getAnswer() + "]";
			String value = UiUtil.readValue(step.getText() + (step.getAnswer()!=null?defaultValue:""));
			if ("-".equals(value)) {
				engine.stepBackward();
				continue;
			}
			if (step.getAnswer()!=null && "".equals(value))
				value = step.getAnswer();
			try {
				engine.assignAnswer(value);
				engine.stepForward();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		engine.startReasoner();
		engine.exportArgumentGraph();
		engine.exportDocument();
		System.out.println("\nExercise finished.");
		
		System.exit(0);
	}

	private static void createExercise() {
		Exercise exercise = new Exercise();

		System.out.println("available goals in rule base:");
		ArrayList<String> goals = RulebaseUtil.retrieveGoals();
		for (int i=0; i<goals.size(); i++)
			System.out.print(i + ":" + goals.get(i) + ",  ");
		System.out.println("");
		String goal = goals.get(UiUtil.readInt("Choice", -1));
		exercise.setRulebaseGoal(goal);
		
		Map<String,String> ruleFacts = RulebaseUtil.retrieveFactsForRulebase();
		for (String factName: ruleFacts.keySet()) {
			RuleFact ruleFact = new RuleFact();
			ruleFact.setName(factName);
			ruleFact.setType(ruleFacts.get(factName));
			exercise.getRuleFacts().add(ruleFact);
		}

		String templateFilename = UiUtil.readValue("document template filename");
		exercise.setTemplateFilename(templateFilename);
		Map<String,String> templateFacts = TemplateUtil.retrieveFactsForTemplate(templateFilename);
		for (String factName: templateFacts.keySet()) {
			TemplateFact templateFact = new TemplateFact();
			templateFact.setName(factName);
			templateFact.setType(ruleFacts.get(factName));
			exercise.getTemplateFacts().add(templateFact);
		}
		System.out.println("---");
		System.out.println("rule base facts:");
		for (int i=0; i<exercise.getRuleFacts().size(); i++) {
			RuleFact ruleFact = exercise.getRuleFacts().get(i);
			System.out.print(i + ":" + ruleFact.getName() + ",  ");
		}
		System.out.println("\n---");
		System.out.println("template facts:");
		for (int i=0; i<exercise.getTemplateFacts().size(); i++) {
			TemplateFact templateFact = exercise.getTemplateFacts().get(i);
			System.out.print(i + ":" + templateFact.getName() + ",  ");
		}
		System.out.println("\n---");
		System.out.println("* fact referencing is performed by ordinary number assigned to them.");
		boolean allFactsIncluded = false;
		int stepIndex = 0;
		while (!allFactsIncluded) {
			Step step = new Step();
			
			int ruleFactNum = UiUtil.readInt("[step=" + stepIndex + "] Rule fact", -1);
			RuleFact ruleFact = ruleFactNum==-1 ? null : exercise.getRuleFacts().get(ruleFactNum);
			step.setRuleFact(ruleFact==null?"":ruleFact.getName());

			int templateFactNum = UiUtil.readInt("[step=" + stepIndex + "] Template fact", -1);
			TemplateFact templateFact = templateFactNum==-1 ? null : exercise.getTemplateFacts().get(templateFactNum);
			step.setTemplateFact(templateFact==null?"":templateFact.getName());
			
			step.setText(UiUtil.readValue("[step=" + stepIndex + "] Question text"));
			exercise.getSteps().add(step);
			stepIndex++;
			
			allFactsIncluded = true;
			for (RuleFact fact: exercise.getRuleFacts()) {
				boolean found = false;
				for (Step s: exercise.getSteps())
					if (fact.getName().equals(s.getRuleFact()))
						found = true;
				if (!found)
					allFactsIncluded = false;
			}
			for (TemplateFact fact: exercise.getTemplateFacts()) {
				boolean found = false;
				for (Step s: exercise.getSteps())
					if (fact.getName().equals(s.getTemplateFact()))
						found = true;
				if (!found)
					allFactsIncluded = false;
			}
		}
		exercise.save(UiUtil.readValue("exercise filename"));

	}


}
