package net.nemerosa.ontrack.jenkins.dsl.facade;

public interface ChangeLogIssueFacade {

    String getKey();
    String getDisplayKey();
    String getSummary();
    String getStatus();
    String getUpdateTime();
    String getUrl();

}