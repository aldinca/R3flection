package ro.teamnet.zth.appl.controller;

import ro.teamnet.zth.api.annotations.*;
import ro.teamnet.zth.appl.domain.Job;
import ro.teamnet.zth.appl.service.JobService;

import java.util.List;

@MyController(urlPath = "/jobs")
public class JobController {
    private final JobService jobService;

    @InjectService
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @MyRequestMethod(urlPath = "/all")
    public List<Job> getAll() {
        return jobService.findAll();
    }

    @MyRequestMethod(urlPath = "/one")
    public Job getOne(@MyRequestParam(name = "id") String jobId) {
        return jobService.findOne(jobId);
    }

//    @MyRequestMethod(urlPath = "/one", methodType = "DELETE")
    @MyRequestMethod(urlPath = "/delete", methodType = "DELETE")
    public Boolean deleteOneEmployee(@MyRequestParam(name = "id") String jobId) {
        return jobService.delete(jobId);
    }

    @MyRequestMethod(urlPath = "/create", methodType = "POST")
    public Job saveJob(@MyRequestObject Job job) {
        return jobService.save(job);
    }

    @MyRequestMethod(urlPath = "/edit", methodType = "PUT")
    public Job updateJob(@MyRequestObject Job job) {
        return jobService.update(job);
    }

}
