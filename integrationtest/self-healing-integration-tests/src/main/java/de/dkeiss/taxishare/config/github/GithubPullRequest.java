package de.dkeiss.taxishare.config.github;

import de.dkeiss.aicurator.config.CodeChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
@Slf4j
public class GithubPullRequest {

    @Value("${taxishare.github.token}")
    private String githubToken;

    @Value("${taxishare.github.repo}")
    private String repoName;

    @Value("${taxishare.github.enabled:false}")
    private boolean pushEnabled;

    @EventListener
    @Async
    public void createPullRequest(CodeChangeEvent codeChangeEvent) throws IOException {
        if(!pushEnabled){
            log.info("CodeChangeEvent: {}", codeChangeEvent);
            return;
        }

        GitHub github = GitHub.connectUsingOAuth(githubToken);
        GHRepository repo = github.getRepository(repoName);

        String newBranchName = "aicurator-fixes_" + new Date().getTime();
        repo.createRef("refs/heads/" + newBranchName, repo.getBranch("master").getSHA1());

        repo.createContent()
                .path(codeChangeEvent.path().toString())
                .content(codeChangeEvent.sourceCode())
                .branch(newBranchName)
                .message("Fixes from AI Curator")
                .commit();

        repo.createPullRequest("Fixes from AI Curator", newBranchName, "master", "Please merge this feature.");
    }

}
