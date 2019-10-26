package com.tour.quest.controlles;

import com.tour.quest.model.Message;
import com.tour.quest.model.User;
import com.tour.quest.repository.MessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
public class MainController {
    @Autowired
    MessagesRepository messagesRepo;

    @Value("${upload.path}")
    private String uploadPath; //looking for property upload.path and add in variable uploadPath

    @GetMapping("/")
    public String greeting(Model model) {
        return "greeting";
    }

    @GetMapping("/main")
    public String main(@RequestParam(required = false, defaultValue = "") String filter, Model model){
        Iterable<Message> messages = messagesRepo.findAll();

        if (filter != null && !filter.isEmpty()) {
            messages = messagesRepo.findByTag(filter);
        } else {
            messages = messagesRepo.findAll();
        }

        model.addAttribute("messages", messages);
        model.addAttribute("filter", filter);
        return "main";
    }
                                                    // todo when use RequestBody instead RequestParam
    @PostMapping("/main")
    public String addMessage(
            @AuthenticationPrincipal User user,
            @RequestParam String text,
            @RequestParam String tag,
            @RequestParam ("file") MultipartFile file, Model model) throws IOException {
        Message message = new Message(text, tag, user);

        if (file != null && !file.getOriginalFilename().isEmpty()){
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()){
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFileName));
            message.setFilename(resultFileName);
        }
        messagesRepo.save(message);

        Iterable<Message> messages = messagesRepo.findAll();
        model.addAttribute("messages", messages);

        return "redirect:/main";
    }
}
