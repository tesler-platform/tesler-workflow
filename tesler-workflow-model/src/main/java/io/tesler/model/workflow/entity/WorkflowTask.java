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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JoinFormula;

@Getter
@Setter
@Entity
@Table(name = "WF_TASK")
public class WorkflowTask extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinFormula("(select ws.id from wf_step ws"
			+ " left join wf_version wv on ws.wf_version_id = wv.id"
			+ " left join wf on wv.wf_id = wf.id"
			+ " where ws.name = step_name and wv.version = version and wf.name = workflow_name)")
	@Setter(AccessLevel.NONE)
	private WorkflowStep workflowStep;

	private transient WorkflowStep temporalWfStep;

	public WorkflowStep getWorkflowStep() {
		if (temporalWfStep != null) {
			return temporalWfStep;
		} else {
			return workflowStep;
		}
	}

	private String stepName;

	private Double version;

	private String workflowName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PENDING_TRANSITION_ID")
	private PendingTransition pendingTransition;

}
