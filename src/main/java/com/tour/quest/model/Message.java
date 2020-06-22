package com.tour.quest.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    @Length(max = 2048, message = "Message to long")
    @NotBlank(message = "Please fill the message")
    private String text;
    private String tag;
    private String filename;
    @ManyToOne(fetch = FetchType.EAGER) //one User has many Messages
    @JoinColumn(name = "user_id")
    private User author;

    public Message() {
    }

    public Message(String text, String tag, User user) {
        this.text = text;
        this.tag = tag;
        this.author = user;
    }

    public String getAuthorName(){
        return author !=null ? author.getUsername(): "<none>";
    }
}
