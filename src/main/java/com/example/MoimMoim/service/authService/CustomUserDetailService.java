package com.example.MoimMoim.service.authService;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.exception.member.MemberNotFoundException;
import com.example.MoimMoim.jwtUtil.CustomUserDetails;
import com.example.MoimMoim.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다."));
        return new CustomUserDetails(member);

    }
}
