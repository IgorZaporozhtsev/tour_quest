package com.tour.quest.controlles;

import com.tour.quest.model.Message;
import com.tour.quest.model.User;
import com.tour.quest.repository.MessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
    public String main(
            Message message,
            @RequestParam(required = false, defaultValue = "") String filter,
            Model model) {
        Iterable<Message> messages = messagesRepo.findAll();

        if (filter != null && !filter.isEmpty()) {
            messages = messagesRepo.findByTag(filter);
        } else {
            messages = messagesRepo.findAll();
        }

        model.addAttribute("messages", messages);
        model.addAttribute( "message", message);
        model.addAttribute("filter", filter);
        return "main";
    }

    // todo when use RequestBody instead RequestParam
    @PostMapping("/main")
    public String addMessage(@AuthenticationPrincipal User user, @Valid Message message, BindingResult bindingResult, Model model, @RequestParam("file") MultipartFile file) throws IOException {
        message.setAuthor(user);

        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtil.getErrors(bindingResult);
            model.addAttribute( "message", message);
            model.mergeAttributes(errorsMap); //todo mergeAttributes не работает Validation из-за mergeAttributes

            //return "main";

        } else {

            if (file != null && !file.getOriginalFilename().isEmpty()) {
                File uploadDir = new File(uploadPath);

                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                String uuidFile = UUID.randomUUID().toString();
                String resultFileName = uuidFile + "." + file.getOriginalFilename();
                file.transferTo(new File(uploadPath + "/" + resultFileName));
                message.setFilename(resultFileName);
            }

            messagesRepo.save(message);
        }

        Iterable<Message> messages = messagesRepo.findAll();
        model.addAttribute("messages", messages);

        return "redirect:/main";
    }


}
