package com.example.MoimMoim.dto.post;

import com.example.MoimMoim.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDTO {

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    @Size(max = 100, message = "제목은 최대 100자까지 입력 가능합니다.")
    private String title;


    /*
    Category는 enum 타입이기 때문에 @NotBlank 대신 @NotNull을 사용해야 한다. @NotBlank는 문자열에만 적용되기 때문에, enum에는 적합하지 않다.
    * */

    @NotNull(message = "카테고리는 필수 입력 항목입니다.")
    private String category;

    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    @Size(max = 3000, message = "내용은 최대 3000자까지 입력 가능합니다.")
    private String content;

    @NotNull(message = "작성자의 ID는 필수 입력 항목입니다.")
    private Long memberId;

    @Override
    public String toString() {
        return "PostWriteRequestDTO{" +
                "title='" + title + '\'' +
                ", category=" + category +
                ", content='" + content + '\'' +
                ", memberId=" + memberId +
                '}';
    }

}
