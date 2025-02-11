package com.adobe.aem.modernize.policy.job;

/*-
 * #%L
 * AEM Modernize Tools - Core
 * %%
 * Copyright (C) 2019 - 2021 Adobe Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.List;
import java.util.Set;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobExecutionContext;
import org.apache.sling.event.jobs.consumer.JobExecutor;

import com.adobe.aem.modernize.RewriteException;
import com.adobe.aem.modernize.job.AbstractConversionJobExecutor;
import com.adobe.aem.modernize.model.ConversionJobBucket;
import com.adobe.aem.modernize.policy.PolicyImportRuleService;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import static com.adobe.aem.modernize.policy.job.PolicyJobExecutor.*;

@Component(
    service = { JobExecutor.class },
    property = {
        JobExecutor.PROPERTY_TOPICS + "=" + JOB_TOPIC
    }
)
public class PolicyJobExecutor extends AbstractConversionJobExecutor {

  public static final String JOB_TOPIC = "com/adobe/aem/modernize/job/topic/convert/policy";

  @Reference
  private PolicyImportRuleService policyService;

  @Reference
  private ResourceResolverFactory resourceResolverFactory;

  @Override
  protected void doProcess(@NotNull Job job, @NotNull JobExecutionContext context, @NotNull ConversionJobBucket bucket) {
    Resource resource = bucket.getResource();
    final List<String> paths = bucket.getPaths();
    context.initProgress(paths.size(), -1);

    final Set<String> rules = getPolicyRules(bucket);
    if (rules.isEmpty()) {
      context.log("No policy rules found, skipping skipping policy import.");
      return;
    }
    ResourceResolver rr = resource.getResourceResolver();
    String target = getTargetConfPath(bucket);
    if (target == null) {
      context.log("No target Conf design specified, skipping policy import.");
      return;
    }
    boolean overwrite = isOverwrite(bucket);
    for (String path : paths) {
      Resource r = rr.getResource(path);
      if (r != null) {
        try {
          policyService.apply(r, target, rules, false, overwrite);
          bucket.getSuccess().add(path);
        } catch (RewriteException e) {
          logger.error("Policy Import resulted in an error", e);
          bucket.getFailed().add(path);
        }
      } else {
        bucket.getNotFound().add(path);
      }
      context.incrementProgressCount(1);
    }

  }

  @Override
  protected ResourceResolverFactory getResourceResolverFactory() {
    return resourceResolverFactory;
  }
}
