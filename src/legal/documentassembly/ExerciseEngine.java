package legal.documentassembly;

import java.text.SimpleDateFormat;

import javax.lang.model.util.SimpleAnnotationValueVisitor6;

import legal.documentassembly.bean.Exercise;
import legal.documentassembly.bean.Step;

/**
 * Exercise engine for document assembly
 *
 */
public class ExerciseEngine {

	private Exercise exercise = null;
	private String exercise_filename;
	private int currentStepIndex;


	/**
	 * The constructor of exercise engine
	 * @param exercise_filename filename of exercise configuration
	 */
	public ExerciseEngine(String exercise_filename) {
		this.exercise_filename = exercise_filename;
		exercise = Exercise.load(exercise_filename);
		currentStepIndex = 0;
	}
	
	/**
	 * Executes reasoning process
	 */
	public void startReasoner() {
		ReasonerUtil.getConclusion(exercise);
	}
	
	/**
	 * Executes building of argument graph
	 */
	public void exportArgumentGraph() {
		ArgumentGraphUtil.buildArgumentGraph();
	}

	/**
	 * Executes document template engine
	 */
	public void exportDocument() {
		TemplateUtil.buildDocument(exercise);
	}
	
	/**
	 * Test if there are more exercise steps
	 * @return true if exercise has next step, false otherwise
	 */
	public boolean hasNextStep() {
		return currentStepIndex + 1 < exercise.getSteps().size();
	}

	/**
	 * Returns current exercise step
	 * @return current Step object
	 */
	public Step getCurrentStep() {
		return exercise.getSteps().get(currentStepIndex);
	}
/*
	// next 3 methods requires -1 as currentStepIndex initial value
	public Step getNextStep() {
		if (hasNextStep())
			return exercise.getSteps().get(++currentStepIndex);
		return null;
	}
	
	public Step getPreviousStep() {
		if (currentStepIndex > 0)
			return exercise.getSteps().get(--currentStepIndex);
		return null;
	}

	public Step getPreviousStep(int index) {
		if (index >= 0 && index < currentStepIndex) {
			currentStepIndex = index;
			return exercise.getSteps().get(currentStepIndex);
		}
		return null;
	}
*/
	/**
	 * Move to the next exercise step
	 * @return true if end of exercise is not reached, false otherwise
	 */
	public boolean stepForward() {
		currentStepIndex++;
		if (endOfExercise())
			return false;
		return true;
	}

	/**
	 * Return to the previous step
	 * @return true if previous step exists, false otherwise
	 */
	public boolean stepBackward() {
		if (currentStepIndex > 0) {
			currentStepIndex--;
			return true;
		}
		return false;
	}
	
	/**
	 * Test if the end pf exercise is reached
	 * @return true if there are no more steps left, false otherwise
	 */
	public boolean endOfExercise() {
		return currentStepIndex >= exercise.getSteps().size();
	}
	
	/**
	 * Assigns answer for the current step
	 * @param answer answer value
	 * @throws Exception if data type validation has failed
	 */
	public void assignAnswer(String answer) throws Exception {
		Step currentStep = exercise.getSteps().get(currentStepIndex);
		String answerType = currentStep.getAnswerType();
		if ("int".equals(answerType)) {
			try {
				Integer.parseInt(answer);
			} catch (Exception e) {
				throw new Exception("Wrong format, expected integer");
			}
		} else if ("float".equals(answerType)) {
			try {
				Float.parseFloat(answer);
			} catch (Exception e) {
				throw new Exception("Wrong format, expected float");
			}
		} else if ("date".equals(answerType)) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
				sdf.parse(answer);
			} catch (Exception e) {
				throw new Exception("Wrong format, expected dd.mm.yyyy.");
			}
		} else if ("time".equals(answerType)) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				sdf.parse(answer);
			} catch (Exception e) {
				throw new Exception("Wrong format, expected hh:mm");
			}
		}
		currentStep.setAnswer(answer);
	}
	
}
