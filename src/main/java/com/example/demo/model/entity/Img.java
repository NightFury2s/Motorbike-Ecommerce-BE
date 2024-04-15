    package com.example.demo.model.entity;

    import lombok.Data;
    import lombok.Getter;
    import lombok.Setter;
    import org.hibernate.annotations.Type;

    import javax.persistence.*;
    import java.sql.Blob;
    import java.sql.Clob;

    import static javax.persistence.FetchType.LAZY;

    @Entity
    @Table(name = "Img")
    @Getter
    @Setter
    public class Img {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)

        private Long idImg;
        //@Lob
        @Type(type = "text")
        @Column(name = "image", columnDefinition = "TEXT")
     //   private String image;
        private String imgData;
        private String content;

    }
