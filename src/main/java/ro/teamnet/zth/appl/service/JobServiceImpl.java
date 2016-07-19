package ro.teamnet.zth.appl.service;

import ro.teamnet.zth.api.annotations.MyService;
import ro.teamnet.zth.appl.dao.JobDao;
import ro.teamnet.zth.appl.domain.Job;

import java.util.List;

/**
 * Created by Oana.Mihai on 7/15/2016.
 */
@MyService
public class JobServiceImpl implements JobService {

    private JobDao jobDao = new JobDao();

    @Override
    public List<Job> findAll() {
        return jobDao.getAllJobs();
    }

    @Override
    public Job findOne(String jobId) {
        return jobDao.getJobById(jobId);
    }

    @Override
    public Boolean delete(String jobId) {
/*Atentie, ca sa poti sterge un manager din bbaza de date, mai intai trebuie sa stergi
 toti oamenii de sub acel mannager, si abia apoi poti sterge managerul respcetiv :)
 Mult Succes !
* */
        Job job = findOne(jobId);
        if (job == null) return false;
        jobDao.deleteJob(job);
        return true;
    }

    @Override
    public Job save(Job job) {
        return jobDao.insertJob(job);
    }

    @Override
    public Job update(Job job) {
        return jobDao.updateJob(job);
    }
}
