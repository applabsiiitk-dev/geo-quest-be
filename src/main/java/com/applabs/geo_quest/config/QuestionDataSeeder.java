package com.applabs.geo_quest.config;

import com.applabs.geo_quest.model.Question;
import com.applabs.geo_quest.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Seeds the GeoQuest question bank into Neo4j on startup.
 *
 * Only runs when the "seed" Spring profile is active, so it won't
 * accidentally re-seed in production.
 *
 * To run:   java -jar geo_quest.jar --spring.profiles.active=seed
 * Or set:   SPRING_PROFILES_ACTIVE=seed in your .env / Docker env.
 *
 * ─────────────────────────────────────────────────────────────────
 * COORDINATE PLACEHOLDERS
 * ─────────────────────────────────────────────────────────────────
 * Every question below uses placeholder GPS coordinates centred on
 * IIIT Kottayam (lat 9.5916, lng 76.5222).  Replace each pair with
 * the real campus location where you want that question to appear.
 *
 * unlockRadius is in metres.  50–100 m works well outdoors.
 * ─────────────────────────────────────────────────────────────────
 */
@Component
@Profile("seed")
public class QuestionDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(QuestionDataSeeder.class);

    private final QuestionRepository questionRepository;

    public QuestionDataSeeder(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (questionRepository.count() > 0) {
            log.info("Questions already exist — skipping seed.");
            return;
        }

        log.info("Seeding {} questions...", ALL_QUESTIONS.size());
        questionRepository.saveAll(ALL_QUESTIONS);
        log.info("Seeding complete.");
    }

    // ──────────────────────────────────────────────────────────────
    // QUESTION BANK
    // Source: Betalabs Questions (Set 1) + Betalabs Additional Set 2
    //
    // difficulty 1 = easy  (score  0–99)
    // difficulty 2 = medium(score 100–249)
    // difficulty 3 = hard  (score 250+)
    //
    // For MCQ questions, options list is provided and correctAnswer
    // matches exactly one option (case-insensitive comparison used
    // at runtime in AnswerService).
    // ──────────────────────────────────────────────────────────────
    private static final List<Question> ALL_QUESTIONS = List.of(

        // ── DIFFICULTY 1 ─────────────────────────────────────────

        q("What does CPU stand for?",
          "Choose the correct expansion.",
          1, 10,
          9.5916, 76.5222, 80,
          "CSE",
          "Central Processing Unit",
          List.of("Central Program Unit", "Central Processing Unit",
                  "Computer Processing Unit", "Control Processing Unit")),

        q("What does RAM stand for?",
          "Choose the correct expansion.",
          1, 10,
          9.5920, 76.5230, 80,
          "CSE",
          "Random Access Memory",
          List.of("Random Access Memory", "Read Access Memory",
                  "Rapid Access Memory", "Run Access Memory")),

        q("Which language is primarily used for web page structure?",
          "Pick the right language.",
          1, 10,
          9.5912, 76.5215, 80,
          "Web",
          "HTML",
          List.of("Python", "HTML", "Java", "C++")),

        q("Which protocol is used to transfer web pages?",
          "Select the correct protocol.",
          1, 10,
          9.5925, 76.5235, 80,
          "Networking",
          "HTTP",
          List.of("FTP", "HTTP", "TCP", "SMTP")),

        q("Which company developed Python?",
          "Who created the Python language?",
          1, 10,
          9.5908, 76.5210, 80,
          "General",
          "Python Software Foundation",
          List.of("Google", "Microsoft", "Python Software Foundation", "Apple")),

        q("Which of these is NOT an operating system?",
          "Identify the odd one out.",
          1, 10,
          9.5930, 76.5240, 80,
          "General",
          "Oracle",
          List.of("Linux", "Windows", "Oracle", "macOS")),

        q("Which memory is fastest?",
          "Pick the fastest type of memory.",
          1, 10,
          9.5918, 76.5228, 80,
          "Hardware",
          "Cache",
          List.of("RAM", "Cache", "Hard Disk", "ROM")),

        q("What does SQL stand for?",
          "Full form of SQL.",
          1, 10,
          9.5922, 76.5218, 80,
          "Databases",
          "Structured Query Language",
          List.of("Structured Query Language", "Simple Query Language",
                  "Standard Query Language", "Sequential Query Language")),

        q("What does API stand for?",
          "Full form of API.",
          1, 10,
          9.5914, 76.5225, 80,
          "General",
          "Application Programming Interface",
          List.of("Application Programming Interface",
                  "Application Process Interface",
                  "Applied Programming Interface",
                  "Application Program Internet")),

        q("What does DNS stand for?",
          "Full form of DNS.",
          1, 10,
          9.5905, 76.5220, 80,
          "Networking",
          "Domain Name System",
          List.of("Domain Name System", "Data Network Service",
                  "Domain Network Server", "Digital Name System")),

        q("What does GPU stand for?",
          "Full form of GPU.",
          1, 10,
          9.5928, 76.5232, 80,
          "Hardware",
          "Graphics Processing Unit",
          List.of("General Processing Unit", "Graphics Processing Unit",
                  "Graph Processing Unit", "Graphic Program Utility")),

        q("What does LAN stand for?",
          "Full form of LAN.",
          1, 10,
          9.5910, 76.5212, 80,
          "Networking",
          "Local Area Network",
          List.of("Local Area Network", "Large Area Network",
                  "Long Area Node", "Local Access Network")),

        q("What does URL stand for?",
          "Full form of URL.",
          1, 10,
          9.5932, 76.5242, 80,
          "Networking",
          "Uniform Resource Locator",
          List.of("Universal Resource Locator", "Uniform Resource Locator",
                  "Unified Resource Locator", "Universal Reference Link")),

        q("What does SSD stand for?",
          "Full form of SSD.",
          1, 10,
          9.5916, 76.5244, 80,
          "Hardware",
          "Solid State Drive",
          List.of("Solid State Drive", "Secure Storage Disk",
                  "System Storage Device", "Solid Storage Disk")),

        q("Which language is mainly used for styling web pages?",
          "Pick the styling language.",
          1, 10,
          9.5902, 76.5218, 80,
          "Web",
          "CSS",
          List.of("HTML", "CSS", "Python", "Java")),

        q("Which protocol is used to send email?",
          "Select the email-sending protocol.",
          1, 10,
          9.5924, 76.5214, 80,
          "Networking",
          "SMTP",
          List.of("HTTP", "FTP", "SMTP", "TCP")),

        q("Which company developed Java?",
          "Who created Java?",
          1, 10,
          9.5934, 76.5236, 80,
          "General",
          "Sun Microsystems",
          List.of("Apple", "Sun Microsystems", "Google", "IBM")),

        q("Which database language is used to retrieve data?",
          "Pick the data-retrieval language.",
          1, 10,
          9.5906, 76.5238, 80,
          "Databases",
          "SQL",
          List.of("SQL", "HTML", "CSS", "XML")),

        // ── DIFFICULTY 2 ─────────────────────────────────────────

        q("What is the average time complexity of Quick Sort?",
          "Choose the correct Big-O.",
          2, 25,
          9.5940, 76.5250, 100,
          "Algorithms",
          "O(n log n)",
          List.of("O(n²)", "O(n log n)", "O(log n)", "O(n)")),

        q("Which traversal of a BST produces sorted output?",
          "Which traversal gives ascending order?",
          2, 25,
          9.5942, 76.5255, 100,
          "Data Structures",
          "Inorder",
          List.of("Preorder", "Inorder", "Postorder", "Level order")),

        q("Which data structure is used in BFS?",
          "BFS uses which structure internally?",
          2, 25,
          9.5944, 76.5260, 100,
          "Data Structures",
          "Queue",
          List.of("Stack", "Queue", "Heap", "Array")),

        q("Which data structure is used in DFS?",
          "DFS uses which structure internally?",
          2, 25,
          9.5938, 76.5248, 100,
          "Data Structures",
          "Stack",
          List.of("Queue", "Stack", "Heap", "Array")),

        q("Which algorithm finds the shortest path with non-negative weights?",
          "Classic shortest-path algorithm.",
          2, 25,
          9.5946, 76.5265, 100,
          "Algorithms",
          "Dijkstra",
          List.of("Kruskal", "Dijkstra", "Prim", "Merge")),

        q("Which sorting algorithm repeatedly selects the smallest element?",
          "Identify the sort by its strategy.",
          2, 25,
          9.5936, 76.5245, 100,
          "Algorithms",
          "Selection Sort",
          List.of("Selection Sort", "Merge Sort", "Heap Sort", "Quick Sort")),

        q("Which data structure stores key-value pairs?",
          "Pick the correct structure.",
          2, 25,
          9.5948, 76.5270, 100,
          "Data Structures",
          "Hash Table",
          List.of("Stack", "Queue", "Hash Table", "Tree")),

        q("Which data structure allows insertion from both ends?",
          "Double-ended structure name.",
          2, 25,
          9.5950, 76.5275, 100,
          "Data Structures",
          "Deque",
          List.of("Queue", "Stack", "Deque", "Heap")),

        q("What is the time complexity of accessing an element by index?",
          "Array index access complexity.",
          2, 25,
          9.5952, 76.5280, 100,
          "Data Structures",
          "O(1)",
          List.of("O(1)", "O(n)", "O(log n)", "O(n log n)")),

        q("Which algorithm is used to find a Minimum Spanning Tree?",
          "Pick one MST algorithm.",
          2, 25,
          9.5954, 76.5285, 100,
          "Algorithms",
          "Kruskal",
          List.of("Kruskal", "Binary Search", "DFS", "Linear Search")),

        q("Which traversal visits root first, then left, then right subtree?",
          "Name this tree traversal.",
          2, 25,
          9.5956, 76.5290, 100,
          "Data Structures",
          "Preorder",
          List.of("Inorder", "Postorder", "Preorder", "Level order")),

        q("Which structure is best for recursive function calls?",
          "What does the call stack use?",
          2, 25,
          9.5958, 76.5295, 100,
          "Data Structures",
          "Stack",
          List.of("Queue", "Stack", "Array", "Graph")),

        q("Which algorithm explores nodes level by level?",
          "Level-order traversal uses which algorithm?",
          2, 25,
          9.5960, 76.5300, 100,
          "Algorithms",
          "BFS",
          List.of("DFS", "BFS", "Dijkstra", "Kruskal")),

        q("Which data structure uses hashing?",
          "Pick the hashing-based structure.",
          2, 25,
          9.5962, 76.5305, 100,
          "Data Structures",
          "Hash Table",
          List.of("Hash Table", "Stack", "Queue", "Tree")),

        q("Which sorting algorithm compares adjacent elements?",
          "Identify the sort by adjacent comparison.",
          2, 25,
          9.5964, 76.5310, 100,
          "Algorithms",
          "Bubble Sort",
          List.of("Bubble Sort", "Merge Sort", "Heap Sort", "Quick Sort")),

        q("Which device connects multiple networks together?",
          "Identifies the networking device.",
          2, 25,
          9.5966, 76.5315, 100,
          "Networking",
          "Router",
          List.of("Switch", "Router", "Hub", "Repeater")),

        q("Which language is widely used for AI and ML?",
          "Most popular AI language.",
          2, 20,
          9.5968, 76.5320, 100,
          "General",
          "Python",
          List.of("Python", "HTML", "CSS", "SQL")),

        q("Which structure represents hierarchical data?",
          "Pick the hierarchical data structure.",
          2, 20,
          9.5970, 76.5325, 100,
          "Data Structures",
          "Tree",
          List.of("Array", "Tree", "Stack", "Queue")),

        q("Which algorithm is used for shortest path with weights?",
          "Classic weighted graph algorithm.",
          2, 25,
          9.5972, 76.5330, 100,
          "Algorithms",
          "Dijkstra",
          List.of("Prim", "Dijkstra", "BFS", "Merge")),

        // ── DIFFICULTY 3 ─────────────────────────────────────────

        q("What is the worst case complexity of Binary Search?",
          "Worst-case Big-O for binary search.",
          3, 50,
          9.5990, 76.5360, 120,
          "Algorithms",
          "O(log n)",
          List.of("O(log n)", "O(n)", "O(n log n)", "O(1)")),

        q("What is the worst case complexity of Bubble Sort?",
          "Worst-case Big-O for bubble sort.",
          3, 50,
          9.5992, 76.5365, 120,
          "Algorithms",
          "O(n²)",
          List.of("O(n)", "O(n log n)", "O(n²)", "O(log n)")),

        q("What is the worst case complexity of Insertion Sort?",
          "Worst-case Big-O for insertion sort.",
          3, 50,
          9.5994, 76.5370, 120,
          "Algorithms",
          "O(n²)",
          List.of("O(n)", "O(log n)", "O(n²)", "O(n log n)")),

        q("Which searching algorithm requires sorted data?",
          "Which search needs a sorted array?",
          3, 50,
          9.5996, 76.5375, 120,
          "Algorithms",
          "Binary Search",
          List.of("Linear Search", "Binary Search", "BFS", "DFS")),

        q("Which component performs arithmetic operations in a CPU?",
          "Name the CPU arithmetic unit.",
          3, 50,
          9.5998, 76.5380, 120,
          "Hardware",
          "ALU",
          List.of("RAM", "Control Unit", "ALU", "Cache")),

        q("Which memory is non-volatile?",
          "Pick the non-volatile memory type.",
          3, 50,
          9.6000, 76.5385, 120,
          "Hardware",
          "ROM",
          List.of("RAM", "Cache", "ROM", "Register")),

        q("Which algorithm finds Minimum Spanning Tree? (Prim's)",
          "Another MST algorithm — not Kruskal.",
          3, 50,
          9.6002, 76.5390, 120,
          "Algorithms",
          "Prim's",
          List.of("Prim's", "Dijkstra", "Binary Search", "BFS")),

        q("Which sorting algorithm uses divide and conquer?",
          "Divide-and-conquer sort.",
          3, 50,
          9.6004, 76.5395, 120,
          "Algorithms",
          "Merge Sort",
          List.of("Bubble Sort", "Selection Sort", "Merge Sort", "Insertion Sort")),

        q("Which structure stores elements in LIFO order?",
          "Name the LIFO data structure.",
          3, 50,
          9.6006, 76.5400, 120,
          "Data Structures",
          "Stack",
          List.of("Queue", "Stack", "Heap", "Tree")),

        // Python output questions ─────────────────────────────────

        q("What is the output of: print(2 + 3 * 4)",
          "Evaluate the Python expression.",
          3, 40,
          9.6008, 76.5405, 120,
          "Python",
          "14",
          List.of("20", "14", "24", "9")),

        q("What is the output of: a = [1,2,3]; print(a[-1])",
          "Python negative indexing.",
          3, 40,
          9.6010, 76.5410, 120,
          "Python",
          "3",
          List.of("1", "2", "3", "Error")),

        q("What error does print(5/0) raise?",
          "Division by zero in Python.",
          3, 40,
          9.6012, 76.5415, 120,
          "Python",
          "ZeroDivisionError",
          List.of("SyntaxError", "ValueError", "ZeroDivisionError", "TypeError")),

        q("What is the output of: print(10//3)",
          "Python floor division.",
          3, 40,
          9.6014, 76.5420, 120,
          "Python",
          "3",
          List.of("3.33", "3", "4", "3.0")),

        q("What is the output of: print(type({}))",
          "Python type of empty braces.",
          3, 40,
          9.6016, 76.5425, 120,
          "Python",
          "dict",
          List.of("list", "set", "dict", "tuple")),

        q("What is the output of: print('Hello'[1])",
          "Python string indexing.",
          3, 40,
          9.6018, 76.5430, 120,
          "Python",
          "e",
          List.of("H", "e", "l", "o")),

        q("What error does int('abc') raise?",
          "Python type conversion error.",
          3, 40,
          9.6020, 76.5435, 120,
          "Python",
          "ValueError",
          List.of("ValueError", "SyntaxError", "TypeError", "KeyError")),

        q("What is the output of: print(9 % 4)",
          "Python modulo operator.",
          3, 40,
          9.6022, 76.5440, 120,
          "Python",
          "1",
          List.of("1", "2", "3", "4")),

        q("What is the output of: print('Data'[2])",
          "Python string character at index 2.",
          3, 40,
          9.6024, 76.5445, 120,
          "Python",
          "t",
          List.of("D", "a", "t", "Error")),

        q("What is the output of: x = 10; x -= 4; print(x)",
          "Python -= operator.",
          3, 40,
          9.6026, 76.5450, 120,
          "Python",
          "6",
          List.of("6", "10", "4", "Error"))
    );

    /** Builder helper to keep the list above readable. */
    private static Question q(String title, String description,
                               int difficulty, int points,
                               double lat, double lng, double radius,
                               String category,
                               String correctAnswer,
                               List<String> options) {
        return Question.builder()
                .title(title)
                .description(description)
                .difficulty(difficulty)
                .points(points)
                .latitude(lat)
                .longitude(lng)
                .unlockRadius(radius)
                .category(category)
                .correctAnswer(correctAnswer)
                .options(options)
                .createdAt(Instant.now())
                .build();
    }
}