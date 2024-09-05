package org.ojt.board_ojt.api.member.dto.req;

import lombok.*;
import org.ojt.board_ojt.api.member.domain.Job;

@Getter
@ToString
@RequiredArgsConstructor
public class PatchInfoReq {

    private String name;
    private Job job;
    private String introduction;
    private String profileImageUrl;


    @Builder
    public PatchInfoReq(String name, Job job, String introduction, String profileImage) {
        this.name = name;
        this.job = job;
        this.introduction = introduction;
        this.profileImageUrl = profileImage;
    }


}
