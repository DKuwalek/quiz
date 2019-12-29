package com.sandura.quiz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

@Controller
public class QuestionController {

    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private QuestionRepository questionRepository;

    @PostMapping(path = "/add") // Map ONLY POST Requests
    public @ResponseBody
    String addNewQuestion(@RequestParam String title, @RequestParam String description) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        Question n = new Question();
        n.setTitle(title);
        n.setDescription(description);
        questionRepository.save(n);

        log.info("Saving Question " + n + " in the database.");

        return "Saved";
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<Question> getAllQuestions() {
        // This returns a JSON or XML with the users
        log.info("Returning all questions");
        return questionRepository.findAll();
    }

    @GetMapping(path = "populate")
    public String populateDatabaseWithQuestionsFromFile(Model model) {
        final ArrayList<Question> questions = new ArrayList<>();
        String pathname = "./src/main/resources/questions.csv";
        File source = new File(pathname);
        Question tmp;
        try {
            Scanner scanner = new Scanner(source);
            scanner.useDelimiter("\n");
            while (scanner.hasNext()) {
                String questionFromFile = scanner.next();
                log.info(questionFromFile);
                tmp = new Question();
                String[] split = questionFromFile.split(",");
                tmp.setTitle(split[0]);
                tmp.setDescription(split[1]);
                questions.add(tmp);
            }
        } catch (FileNotFoundException fileNotFoundException) {
            log.error("Exception occured while reading file {}", pathname);
            log.error(fileNotFoundException.toString());
        }
        questionRepository.saveAll(questions);
        model.addAttribute("pathname", source.getAbsoluteFile());
        return "populate";
    }

}
