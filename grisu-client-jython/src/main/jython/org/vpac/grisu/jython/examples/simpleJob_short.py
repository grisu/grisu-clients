from org.vpac.grisu.frontend.control.login import LoginManager
from org.vpac.grisu.frontend.model.job import JobObject

si = LoginManager.loginCommandline("BeSTGRID")

job = JobObject(si);
job.setUniqueJobname("cat_job")
job.setCommandline("cat singleJobFile_0.txt")
job.addInputFileUrl('/home/markus/test/singleJobFile_0.txt');

job.createJob("/ARCS/BeSTGRID")
job.submitJob()

job.waitForJobToFinish(10)

print 'Job finished. Status: '+job.getStatusString(False)
print "Stdout: " + job.getStdOutContent()
print "Stderr: " + job.getStdErrContent()

job.kill(True)

