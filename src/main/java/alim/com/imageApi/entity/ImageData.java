package alim.com.imageApi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "imageData")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_data_id")
    private Long id;

    @Column(length = 30,unique = true,nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String access;

    @Column(nullable = false)
    private String state;

    @Lob
    @Column(name = "image_data",length = 1000,nullable = false)
    private byte[] imageData;

    @Column(name = "created_time",nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "update_time",nullable = false)
    private LocalDateTime updateTime;

    @ManyToOne
    @JoinColumn(name = "person_id",referencedColumnName = "person_id")
    private Person person;
}
