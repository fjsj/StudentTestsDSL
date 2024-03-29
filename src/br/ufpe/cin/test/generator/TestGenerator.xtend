/*
 * generated by Xtext
 */
package br.ufpe.cin.test.generator

import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.IGenerator
import org.eclipse.xtext.generator.IFileSystemAccess

class TestGenerator implements IGenerator {
	
	override void doGenerate(Resource resource, IFileSystemAccess fsa) {
		fsa.generateFile(
			"QuizGame.java",
			toJavaCode(resource.contents.head as br.ufpe.cin.test.test.Main)
		)
	}
	
	def generateMain(br.ufpe.cin.test.test.Main m) '''
	public static void main(String[] args) {
		� FOR test : m.tests �
			Test �test.name� = new Test("�test.subject�", "�test.name�");
			
			Question q = null;
			� FOR question : test.elements �
				q = new Question(�question.type.value�, "�question.name�",
						"�question.text�", �question.score�);
						
				� FOR choice : question.elements �
					q.addChoice(new Choice("�choice.text�", �choice.correct�));
				� ENDFOR �
				�test.name�.addQuestion(q);
			� ENDFOR �
		� ENDFOR �
		
		classes = new ArrayList<Class>();
		� FOR clazz : m.classes �
			Class �clazz.name� = new Class("�clazz.name�");
			
			� FOR student : clazz.elements �
				new Student("�student.fullName�", "�student.name�", "�student.text�", �clazz.name�);
			� ENDFOR �
			
			classes.add(�clazz.name�);	
		� ENDFOR �
		
		� FOR apply : m.commands �
			�apply.className�.addTest(�apply.testName�);
		� ENDFOR �
		
		clazz.addTest(t);
		
		new Main();
	}
	'''
	
	def toJavaCode(br.ufpe.cin.test.test.Main m) '''
	import java.io.File;
	import java.io.FileInputStream;
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
		private static ArrayList<Class> classes;
		private Scanner input;
	
		public Main() {
			System.out.println("Are you a student or teacher?");
			System.out.println("0 - student");
			System.out.println("1 - teacher");
			for (Class clazz : classes) {
				List<Test> tests = clazz.getTests();
				for (Test test : tests) {
					File f = new File(getFilePath(test.getName()));
					if (!f.exists()) {
						saveGradeFile(test.getName(), new ArrayList<Grade>());
					}
				}
			}
			input = new Scanner(System.in);
			int nextInt = input.nextInt();
			if (nextInt == 0) {
				mainStudent();
			} else {
				mainTeacher();
			}
		}
	
		private void mainStudent() {
			System.out.print("Login:");
			String login = input.next();
			System.out.print("Password:");
			String password = input.next();
	
			Student loggedStudent = null;
			for (Class clazz : classes) {
				List<Student> students = clazz.getStudents();
				for (Student student : students) {
					if (student.getName().equals(login) && student.getPassword().equals(password)) {
						loggedStudent = student;
					}
				}
			}
	
			if (loggedStudent == null) {
				System.out.println("Invalid Login/Password");
				mainStudent();
			} else {
				List<Test> tests = loggedStudent.getStudentClass().getTests();
				if (tests.size() > 0) {
					chooseTest(loggedStudent, tests);
				} else {
					System.out.println("There is no available tests for this class");
				}
	
			}
		}
	
		private void chooseTest(Student loggedStudent, List<Test> tests) {
			int qtdTests = 0;
			for (int i = 0; i < tests.size(); i++) {
				Test test = tests.get(i);
				if (!hasDoneTest(loggedStudent.getName(), test.getName())) {
					System.out.println(i + " - " + test.getName());
					qtdTests++;
				}
			}
			if (qtdTests > 0) {
				System.out.println("Choose the test: ");
				int testNumber = input.nextInt();
				if (testNumber >= 0 && testNumber < tests.size()) {
					Test test = tests.get(testNumber);
					int score = applyTest(test);
					String testName = test.getName();
	
					Grade grade = new Grade(loggedStudent.getName(), score + "");
					List<Grade> grades = readGradeFile(testName);
					grades.add(grade);
					saveGradeFile(testName, grades);
				} else {
					System.out.println("Invalid Test");
					chooseTest(loggedStudent, tests);
				}
			} else {
				System.out.println("No Tests Available.");
			}
		}
	
		private void saveGradeFile(String testName, List<Grade> grades) {
			ObjectOutputStream oos = null;
			String filePath = getFilePath(testName);
			try {
				oos = new ObjectOutputStream(new FileOutputStream(filePath));
				oos.writeObject(grades);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (oos != null) {
					try {
						oos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	
		private boolean hasDoneTest(String student, String testName) {
			List<Grade> grades = readGradeFile(testName);
			boolean found = false;
			if (grades != null) {
				for (Grade grade : grades) {
					if (grade.studentName.equals(student)) {
						found = true;
					}
				}
			}
			return found;
		}
	
		private String getFilePath(String testName) {
			return System.getProperty("user.dir") + File.separator + testName
					+ ".bin";
		}
	
		@SuppressWarnings("unchecked")
		private List<Grade> readGradeFile(String testName) {
			String filePath = getFilePath(testName);
			File testFile = new File(filePath);
			ObjectInputStream oi;
			try {
				oi = new ObjectInputStream(new FileInputStream(testFile));
				return (ArrayList<Grade>) oi.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	
		private void mainTeacher() {
			System.out.println("Please type the test name:");
			String testName = input.next();
			List<Grade> grades = readGradeFile(testName);
			if (grades == null) {
				System.out.println("Invalid Test");
				mainTeacher();
			} else {
				for (Grade grade : grades) {
					System.out.println(grade.studentName + " - " + grade.score);
				}
			}
	
		}
		
		�m.generateMain�
	
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
			clazz.addStudent(this);
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
	
	class Grade implements Serializable {
		private static final long serialVersionUID = 1L;
		String studentName;
		String score;
	
		public Grade(String studentName, String score) {
			this.studentName = studentName;
			this.score = score;
		}
	
	}
	'''
}
