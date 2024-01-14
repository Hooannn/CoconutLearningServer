package com.ht.elearning.assignment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/assignments")
@CrossOrigin
public class AssignmentController {
    private final AssignmentService assignmentService;
}
