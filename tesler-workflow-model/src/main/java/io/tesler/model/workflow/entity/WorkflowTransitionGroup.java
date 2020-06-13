/*-
 * #%L
 * IO Tesler - Workflow Model
 * %%
 * Copyright (C) 2018 - 2019 Tesler Contributors
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

package io.tesler.model.workflow.entity;

import io.tesler.model.core.entity.BaseEntity;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "WF_TRANSITION_GROUP")
public class WorkflowTransitionGroup extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "WF_STEP_ID", nullable = false)
	private WorkflowStep workflowStep;

	@Column(name = "MAX_SHOW_BUTTONS_IN_GROUP")
	private int maxShowButtonsInGroup;

	@Column(name = "MORE_NAME")
	private String nameButtonYet;

	@Column(name = "DESCRIPTION")
	private String description;

	@OneToMany(mappedBy = "workflowTransitionGroup", cascade = {CascadeType.DETACH})
	private Set<WorkflowTransition> workflowTransitions;

	@PreRemove
	private void preRemove() {
		workflowTransitions.forEach(wt -> wt.setWorkflowTransitionGroup(null));
	}

}
