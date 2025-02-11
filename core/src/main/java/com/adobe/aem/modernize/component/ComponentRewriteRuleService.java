package com.adobe.aem.modernize.component;

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

import java.util.Set;

import org.apache.sling.api.resource.Resource;

import com.adobe.aem.modernize.RewriteException;
import com.adobe.aem.modernize.rule.RewriteRuleService;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides a mechanism for listing all configured rules either via Nodes or custom implementations.
 */
@ProviderType
public interface ComponentRewriteRuleService extends RewriteRuleService {

  /**
   * Applies the indicated rules to the provided resource. If {@code deep} is set, rules will be applied recursively.
   * <p>
   * Transformations are performed but not saved.
   * <p>
   * The rules can be either a fully qualified path to a rule or a Service PID depending on the implementation.
   * <p>
   * Implementations decide how to handle rule paths which are invalid for their context.
   *
   * @param resource Parent node for applying rules
   * @param rules    the rules to apply
   * @param deep     {@code true} to recurse into the tree
   * @throws RewriteException if any errors occur when applying the rules
   */
  // TODO: Consider returning list of paths that were processed for reporting
  void apply(@NotNull final Resource resource, @NotNull final Set<String> rules, boolean deep) throws RewriteException;

}
