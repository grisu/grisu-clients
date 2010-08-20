'''
Created on 17/11/2009

For documentation on what methods are available for the MultiPartJob object, have a look here:
https://code.arcs.org.au/hudson/job/Grisu-SNAPSHOT/javadoc/index.html?org/vpac/grisu/backend/model/job/MultiPartJob.html
and here for a normal job object:
https://code.arcs.org.au/hudson/job/Grisu-SNAPSHOT/javadoc/org/vpac/grisu/frontend/model/job/JobObject.html

@author: Markus Binsteiner
'''

from au.org.arcs.jcommons.constants import Constants
from org.vpac.grisu.control import ResubmitPolicy, ResubmitPolicy, \
    DefaultResubmitPolicy
from org.vpac.grisu.control.exceptions import NoSuchJobException
from org.vpac.grisu.frontend.control.login import LoginManager, LoginParams
from org.vpac.grisu.frontend.model.job import JobObject, BatchJobObject, \
    JobsException
from org.vpac.grisu.frontend.view.swing.jobmonitoring.batch import \
    BatchJobDialog
from org.vpac.grisu.model import GrisuRegistryManager
import sys
import time

si = LoginManager.loginCommandline("ARCS")

# how many jobs do we want
numberOfJobs = 34

# the (unique) name of the multijob
multiJobName = "34javaResubmitTest";

# delete an (possibly existing) old job with the same name
try:
    si.kill(multiJobName, True);
    
    status = si.getActionStatus(multiJobName)
    while not status.isFinished():
        percentage = status.getCurrentElements() * 100 / status.getTotalElements()
        print "Deletion "+str(percentage)+"% finished."
        time.sleep(3)

except:
    print "No need to kill and clean job " + multiJobName
    

# to see whats going on we add a simple event listener. Hm. This doesn't seem to work reliably in jython. 
#SystemOutMultiJobLogger(multiJobName)

# create the multipart job 
multiPartJob = BatchJobObject(si, multiJobName, "/ARCS/NGAdmin", "java", Constants.NO_VERSION_INDICATOR_STRING);

# if you want to display a gui element to control/monitor the job, use the following line
BatchJobDialog.open(si, multiJobName)

# not needed anymore. it's the default now
multiPartJob.addJobProperty(Constants.DISTRIBUTION_METHOD, Constants.DISTRIBUTION_METHOD_EQUAL);

# now we can calculate the relative path (from every job directory) to the common input file folder
pathToInputFiles = multiPartJob.pathToInputFiles()

for i in range(0, numberOfJobs):
    # create a unique jobname for every job
    jobname = multiJobName+"_"+ str(i)
    # create the single job
    job = JobObject(si)
    #job.setSubmissionLocation("")
    job.setJobname(jobname)
    # better to set the application to use explicitely because in that case we don't need to use mds (faster)
    job.setApplication("java")
    job.setCommandline("java -version")
    # setting the commandline. In this example we reference to both types of possible input files
    #job.setCommandline("java -jar "+pathToInputFiles+"/JavaTestJob.jar 30 1")
    # adding a job-specific input file
    #job.addInputFileUrl("/home/markus/test/singleJobFile.txt")
    # the walltime for the job. Can be set individually or for the multijob (which would have to be done later)
    job.setWalltimeInSeconds(60)
    # adding the job to the multijob
    multiPartJob.addJob(job)
    
multiPartJob.setDefaultNoCpus(1);
multiPartJob.setDefaultWalltimeInSeconds(60);   
    
# now we add an input file that is common to all jobs
#multiPartJob.addInputFile("/home/markus/sample input files/JavaTestJob.jar");
# we don't want to submit to tpac because it doesn't work
#multiPartJob.setSitesToExclude(["uq", "hpsc", "auckland", "canterbury"]);
    
try:
    print "Creating jobs on the backend and staging files..."
    multiPartJob.prepareAndCreateJobs(True)
except (JobsException), error:
    for job in error.getFailures().keySet():
        print "Job: "+job.getJobname()+", Error: "+error.getFailures().get(job).getLocalizedMessage()

    sys.exit()

print "Job distribution:"
for subLoc in multiPartJob.getOptimizationResult().keySet():
    print subLoc + " : " +multiPartJob.getOptimizationResult().get(subLoc)


print "Submitting jobs..."
multiPartJob.submit()

restarted = False

# now we wait for all jobs to finish. Actually, we probably should test whether the job was successful as well...
while not multiPartJob.isFinished(True):
    # printing some stats
    print multiPartJob.getProgress()
    
    # restart failed jobs everytime
    failedpolicy = DefaultResubmitPolicy()
    # to only resubmit failed jobs, we have to remove the waiting jobs resubmission that is set by default
    multiPartJob.restart(failedpolicy, True)

    # restart once after the jobsubmission is finished to optimize job distributions to queues where the job actually runs
    if not restarted:
        
        # actually, it probably would be a good idea to refresh the job status here because otherwise the restart will just 
        # restart failed jobs that were already submitted with the restart above...  not really sure...
        #multiPartJob.refresh()
        
        # this might not work the first few times because in the background the batchjob is still submitting...
        print "trying to restarting job..."
        
        policy = DefaultResubmitPolicy()
        # the next line doesn't make sense since it's the default anyway. Just to demonstrate.
        policy.setProperty(DefaultResubmitPolicy.RESTART_WAITING_JOBS, True)
        restarted = multiPartJob.restart(policy, True)

        if restarted:
            print "Job distribution for restarted jobs:"
            for subLoc in multiPartJob.getOptimizationResult().keySet():
                resubmitted = True
                print subLoc + " : " +multiPartJob.getOptimizationResult().get(subLoc)
        else:
            print "Job not restarted (yet)."
    
    print "Job not finished yet. Waiting..."
    time.sleep(3)

print "Multipartjob "+multiPartJob.getBatchJobname()+" finished."

# finally, everything is ready. We could do a lot more here, but you get the idea...
for job in multiPartJob.getJobs():
    print "Job: "+job.getJobname()+", Status: "+job.getStatusString(False)
    print
    print "Stdout: "
    print job.getStdOutContent()
    print
    print "Stderr: "
    print job.getStdErrContent()
    print
    print
    
print "Finished."
    
# don't forget to exit properly. this cleans up possible existing threads/executors
sys.exit()


