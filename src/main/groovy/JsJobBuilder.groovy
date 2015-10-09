import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job
import ScmUtils
import BaseJobBuilder

class JsJobBuilder {

    String name
    String description
    String gitBranch = 'master'
    String pollScmSchedule = '@daily'
    String tasks
    String junitResults = '**/build/test-results/*.xml'
    String artifacts = 'dist/'
    List<String> emails
    Boolean use_versions

    def repos = [];

    Job build(DslFactory dslFactory) {
        dslFactory.job(name) {
            it.description this.description
            BaseJobBuilder.addBaseStuff(delegate, this.emails)
            wrappers {
                nodejs('Node 0.12')// pass in the version?
            }

            multiscm {
                ScmUtils.project_repos(delegate, this.repos, use_versions)
            }

            triggers {
                scm pollScmSchedule
            }
            steps {
                shell( //we can potentially pass those in as well - $DIR_TO_BUILD and build script name
                        '''
                            cd $DIR_TO_BUILD
                             ./frontendbuild.sh
                        '''
                )
            }
            publishers {
                archiveArtifacts artifacts
            }

        }
    }
}

