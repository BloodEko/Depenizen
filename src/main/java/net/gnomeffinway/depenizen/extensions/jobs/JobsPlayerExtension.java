package net.gnomeffinway.depenizen.extensions.jobs;

import me.zford.jobs.Jobs;
import me.zford.jobs.container.Job;
import me.zford.jobs.container.JobsPlayer;
import net.aufdemrand.denizen.objects.dObject;
import net.aufdemrand.denizen.objects.dPlayer;
import net.aufdemrand.denizen.tags.Attribute;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.gnomeffinway.depenizen.extensions.dObjectExtension;
import net.gnomeffinway.depenizen.objects.jobs.JobsJob;

public class JobsPlayerExtension extends dObjectExtension {

    public static boolean describes(dObject pl) {
        return pl instanceof dPlayer;
    }

    public static JobsPlayerExtension getFrom(dObject pl) {
        if (!describes(pl)) return null;
        else return new JobsPlayerExtension((dPlayer) pl);
    }

    private JobsPlayerExtension(dPlayer pl) {
        player = Jobs.getPlayerManager().getJobsPlayerOffline(pl.getOfflinePlayer());
    }

    JobsPlayer player = null;

    @Override
    public String getAttribute(Attribute attribute) {

        // <--[tag]
        // @attribute <p@player.jobs[<job>]>
        // @returns dJob
        // @description
        // Returns the job specified with the player's information attached.
        // @plugin Jobs
        // -->
        if (attribute.startsWith("jobs")) {
            Job job = null;
            if (attribute.hasContext(1)) {
                job = Jobs.getJob(attribute.getContext(1));
            }
            if (job == null) {
                dB.echoError("Invalid or missing job specified!");
                return null;
            }
            return new JobsJob(job, player).getAttribute(attribute.fulfill(1));
        }

        return null;

    }

}