package alim.com.imageApi.entity;

import jakarta.persistence.*;
import java.util.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "person")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Long personId;

    @Column(name = "user_email",unique = true,nullable = false)
    private String userEmail;

    @Column(name = "first_name",length = 20,nullable = false)
    private String firstName;

    @Column(name = "last_name",length = 20,nullable = false)
    private String lastName;

    @Column(length = 20,nullable = false)
    private String patronymic;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_who",nullable = false)
    private String createdWho;

    @Column(name = "created_time",nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "update_time",nullable = false)
    private LocalDateTime updateTime;

    @OneToMany(mappedBy = "person")
    private List<ImageData> images;
}
