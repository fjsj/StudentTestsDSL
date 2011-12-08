import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Collections;

public class Main {
	private ArrayList<Grade> grades;
	private ArrayList<Class> classes;
	private Scanner input;
	public Main() {

	}
	private void mainStudent(){
		System.out.print("Login:");
		String login = input.next();
		System.out.print("Password:");
		String password = input.next();

		Student loggedStudent = null;
		for (Class clazz : classes) {
			List<Student> students = clazz.getStudents();
			for (Student student : students) {
				if(student.getName().equals(login) && student.getPassword().equals(password)){
					loggedStudent = student;
				} 
			}
		}

		if(loggedStudent == null){
			System.out.println("Invalid Login/Password");
			mainStudent();

		} else{
			List<Test> tests = loggedStudent.getStudentClass().getTests();
			if(tests.size() > 0){
				chooseTest(loggedStudent, tests);
				
			} else{
				System.out.println("There is no available tests for this class");
			}

		}
	}
	
	private void chooseTest(Student loggedStudent, List<Test> tests) {
		System.out.println("Choose the test: ");
		for (int i = 0; i < tests.size(); i++) {
			Test test = tests.get(i);
			
			System.out.println(i + " - " + test.getName());
		}
		
		int testNumber = input.nextInt();
		if(testNumber > 0 && testNumber < tests.size()){
			Test test = tests.get(testNumber);
			 int score = applyTest(test);
			 String testName =test.getName();
			 
			 Grade grade = new Grade(loggedStudent.getName(), testName, score+"");
			 ObjectOutputStream oos = null;
			 try {
				oos = new ObjectOutputStream(new FileOutputStream(testName + ".bin", true));
				oos.writeObject(grade);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				if(oos != null){
					try {
						oos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			 
		} else{
			System.out.println("Invalid Test");
			chooseTest(loggedStudent, tests);
		}
	}
	private void teste() {
		System.out.println("Are you a student or teacher?");
		System.out.println("0 - student");
		System.out.println("1 - teacher");

		input = new Scanner(System.in);
		int nextInt = input.nextInt();
		if(nextInt == 0){
			mainStudent();
		} else {
			mainTeacher();
		}

	}
	
	
	private List<Grade> getGradesFor(String testName){
		
		String filePath = System.getProperty("user.dir") + File.pathSeparator + testName + ".bin";
		File testFile = new File(filePath);
		
		if(testFile.exists()){
			
			ObjectInputStream oi; //TODO: parou aqui
		}
		return null;
	}
	
	private void mainTeacher() {
		
		System.out.println("Please type the test name:");
		String testName = input.next();
		
	}
	public static void main(String[] args) {
		Test t = new Test("The Beatles", "TheBeatlesQuiz");
		Question q = null;
		q = new Question(QuestionType.SINGLE_CHOICE, "1",
				"What is the name of the first Beatles album?", 300);
		q.addChoice(new Choice("Let it be", false));
		q.addChoice(new Choice("Please Please Me", true));
		q.addChoice(new Choice("With The Beatles", false));
		t.addQuestion(q);
		q = new Question(QuestionType.TRUE_OR_FALSE, "2",
				"Which of these names are of past Beatles members?", 100);
		q.addChoice(new Choice("Stuart Sutcliffe", true));
		q.addChoice(new Choice("Pete Best", true));
		q.addChoice(new Choice("George Harrison", false));
		q.addChoice(new Choice("Brian Epstein", false));
		t.addQuestion(q);
		q = new Question(QuestionType.SINGLE_CHOICE, "3",
				"Who is considered the best Beatles producer?", 100);
		q.addChoice(new Choice("George Martin", true));
		q.addChoice(new Choice("Bert Kaempfert", false));
		q.addChoice(new Choice("Neil Aspinall", false));
		q.addChoice(new Choice("Phil Spector", false));
		t.addQuestion(q);
		q = new Question(QuestionType.SINGLE_CHOICE, "4",
				"Which was the year of the last Beatles live performance?", 100);
		q.addChoice(new Choice("1965", false));
		q.addChoice(new Choice("1966", false));
		q.addChoice(new Choice("1967", false));
		q.addChoice(new Choice("1968", false));
		q.addChoice(new Choice("1969", true));
		t.addQuestion(q);

	}
	private int applyTest(Test t) {
		System.out.println(String.format("Test %s of %s", t.getName(), t.getSubject()));
		Scanner input = new Scanner(System.in);
		int totalScore = t.getTotalScore();
		int userScore = 0;
		for (Question question : t.getQuestions()) {
			System.out.println(question.getText());
			List<Choice> choices = question.getChoices();
			for (int i = 0; i < choices.size(); i++) {
				System.out.println(i + " " + choices.get(i).getText());
			}

			String choosenStr = input.next();
			List<Integer> choosen = new ArrayList<Integer>();
			for (String s : choosenStr.split(",")) {
				choosen.add(Integer.parseInt(s));
			}
			Collections.sort(choosen); // sort to ignore ordering
			if (choosen.equals(question.getCorrectChoices())) {
				System.out.println("Right answer!");
				userScore += question.getScore();
			} else {
				System.out.println("Wrong answer!");
			}

		}
		System.out.println(String.format("You got %d%% of the total score.",
				(100 * userScore) / totalScore));
		return (int) ((100 * userScore) / totalScore);
	}
}

class Test {
	private String subject;
	private String name;
	private List<Question> questions;

	public Test(String subject, String name) {
		this.name = name;
		this.subject = subject;
		this.questions = new ArrayList<Question>();
	}

	public void addQuestion(Question q) {
		questions.add(q);
	}

	public String getName() {
		return name;
	}

	public String getSubject() {
		return subject;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public int getTotalScore() {
		int score = 0;
		for (Question q : questions) {
			score += q.getScore();
		}
		return score;
	}
}

class Class {
	private String name;
	private List<Student> students;
	private List<Test> tests;

	public Class(String name) {
		this.name = name;
		this.students = new ArrayList<Student>();
		this.tests = new ArrayList<Test>();
	}

	public String getName() {
		return name;
	}

	public void addStudent(Student s) {
		students.add(s);
	}

	public void addTest(Test t) {
		tests.add(t);
	}

	public List<Student> getStudents() {
		return students;
	}

	public List<Test> getTests() {
		return tests;
	}
}

class Student {
	private String fullName;
	private String name;
	private String password;
	private Class clazz;

	public Student(String fullName, String name, String password, Class clazz) {
		this.fullName = fullName;
		this.name = name;
		this.password = password;
		this.clazz = clazz;
	}

	public String getFullName() {
		return fullName;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public Class getStudentClass() {
		return clazz;
	}
}

enum QuestionType {
	SINGLE_CHOICE, TRUE_OR_FALSE
}

class Question {
	private QuestionType type;
	private String name;
	private String text;
	private int score;
	private List<Choice> choices;

	public Question(QuestionType type, String name, String text, int score) {
		this.type = type;
		this.name = name;
		this.text = text;
		this.score = score;
		this.choices = new ArrayList<Choice>();
	}

	public void addChoice(Choice c) {
		choices.add(c);
	}

	public QuestionType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getText() {
		return text;
	}

	public int getScore() {
		return score;
	}

	public List<Choice> getChoices() {
		return choices;
	}

	public List<Integer> getCorrectChoices() {
		List<Integer> correctOnes = new ArrayList<Integer>();
		for (int i = 0; i < choices.size(); i++) {
			if (choices.get(i).isCorrect()) {
				correctOnes.add(i);
			}
		}
		return correctOnes;
	}
}

class Choice {
	private String text;
	private boolean isCorrect;

	public Choice(String text, boolean isCorrect) {
		this.text = text;
		this.isCorrect = isCorrect;
	}

	public String getText() {
		return text;
	}

	public boolean isCorrect() {
		return isCorrect;
	}
}

class Grade implements Serializable{
	String studentName;
	String test;
	String score;
	
	public Grade(String studentName, String test, String score){
		this.studentName = studentName;
		this.test = test;
		this.score = score;
	}

}
