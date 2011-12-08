package br.ufpe.cin.test.validation;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.validation.Check;

import br.ufpe.cin.test.test.Class;
import br.ufpe.cin.test.test.Main;
import br.ufpe.cin.test.test.Bool;
import br.ufpe.cin.test.test.Choice;
import br.ufpe.cin.test.test.Command;
import br.ufpe.cin.test.test.Question;
import br.ufpe.cin.test.test.Test;
import br.ufpe.cin.test.test.TestPackage;
import br.ufpe.cin.test.test.Type;


public class TestJavaValidator extends AbstractTestJavaValidator {
	EList<Class> classes;
	private EList<Test> tests;
	@Check
	public void checkHasCorrectAnswer(Question question) {
		if ( question.getType().getValue() == Type.SINGLE_CHOICE_VALUE){


			boolean hasCorrectAnswer = false;
			EList<Choice> elements = question.getElements();
			for (Choice choice : elements) {
				if(choice.getCorrect().getValue() == Bool.TRUE_VALUE){
					hasCorrectAnswer = true;
				}
			}

			if (!hasCorrectAnswer) {
				error("Every single choice question must have one correct answer.", TestPackage.Literals.QUESTION__TYPE);
			}
		}
	}

	@Check
	public void getClassesAndTesta(Main main) {
		classes = main.getClasses();
		tests = main.getTests();
		
	}
	
	@Check
	public void checkHasValidClassName(Command command) {
		boolean hasValidClassName = false;
		String className = command.getClassName();
		for (Class clazz : classes) {
			if(clazz.getName().equals(className)){
				hasValidClassName = true;
			}
		}
		
		if(!hasValidClassName){
			error("Class with this ID not defined", TestPackage.Literals.COMMAND__CLASS_NAME);
		}
	}
	
	@Check
	public void checkHasValidTestName(Command command) {
		boolean hasValidTestName = false;
		String testName = command.getTestName();
		for (Test test : tests) {
			if(test.getName().equals(testName)){
				hasValidTestName = true;
			}
		}
		
		if(!hasValidTestName){
			error("Test with this ID not defined", TestPackage.Literals.COMMAND__TEST_NAME);
		}
	}
}
