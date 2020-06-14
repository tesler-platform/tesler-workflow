/*-
 * #%L
 * IO Tesler - Workflow API
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

package io.tesler.source;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

import io.tesler.api.data.dto.rowmeta.ActionDTO;
import io.tesler.core.service.action.ActionScope;
import io.tesler.model.workflow.entity.WorkflowTransition;
import io.tesler.model.workflow.entity.WorkflowTransitionGroup;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.ListUtils;

@UtilityClass
public class WorkflowActionUtils {

	private final String WF_TRANSITION_ID = "wf_";

	private final ActionGroup WITHOUT_GROUP = new ActionGroup(null, null, null, 0);

	public boolean isTransitionAction(final String actionName) {
		return actionName.startsWith(WF_TRANSITION_ID);
	}

	public String actionNameFromTransitionName(final WorkflowTransition transition) {
		return WF_TRANSITION_ID + transition.getName();
	}

	public String transitionNameFromActionName(final String actionName) {
		return actionName.replace(WF_TRANSITION_ID, "");
	}

	public List<ActionDTO> getActions(final List<WorkflowTransition> transitions) {
		final Map<ActionGroup, List<WorkflowTransition>> transitionGroup = transitions.stream().collect(groupingBy(
				transition -> ofNullable(transition.getWorkflowTransitionGroup()).map(ActionGroup::new).orElse(WITHOUT_GROUP),
				mapping(Function.identity(), Collectors.toList())
		));
		final List<ActionDTO> actionsWithoutGroup = getActionsWithoutGroup(transitionGroup.remove(WITHOUT_GROUP));
		return ListUtils.union(getActionWithGroup(transitionGroup), actionsWithoutGroup);
	}

	private List<ActionDTO> getActionWithGroup(final Map<ActionGroup, List<WorkflowTransition>> transitionGroup) {
		return transitionGroup.entrySet().stream()
				.sorted((o1, o2) -> Long.valueOf(o1.getKey().getSeq() - o2.getKey().getSeq()).intValue())
				.map(entry -> {
					ActionDTO actionDTO = new ActionDTO(
							entry.getKey().getType(),
							entry.getKey().getText(),
							entry.getKey().getMaxGroupVisualButtonsCount(),
							getActionsWithoutGroup(entry.getValue())
					);
					actionDTO.setAvailable(true);
					actionDTO.setScope(ActionScope.RECORD.toString().toLowerCase());
					return actionDTO;
				})
				.collect(Collectors.toList());
	}

	private List<ActionDTO> getActionsWithoutGroup(final List<WorkflowTransition> transitions) {
		if (transitions != null) {
			return transitions.stream()
					.sorted((o1, o2) -> {
						int o1Seq = Optional.of(o1).map(WorkflowTransition::getSeq).map(Long::intValue).orElse(0);
						int o2Seq = Optional.of(o2).map(WorkflowTransition::getSeq).map(Long::intValue).orElse(0);
						return o1Seq - o2Seq;
					})
					.map(transition -> {
						ActionDTO actionDTO = new ActionDTO(actionNameFromTransitionName(transition), transition.getText());
						actionDTO.setAvailable(true);
						actionDTO.setScope(ActionScope.RECORD.toString().toLowerCase());
						actionDTO.setIcon(transition.getIconCode());
						return actionDTO;
					})
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	@Getter
	@EqualsAndHashCode
	@RequiredArgsConstructor
	private class ActionGroup {

		private final String type;

		private final String text;

		private final Integer maxGroupVisualButtonsCount;

		private final long seq;

		private ActionGroup(final WorkflowTransitionGroup transitionGroup) {
			this(
					transitionGroup.getNameButtonYet(),
					transitionGroup.getDescription(),
					transitionGroup.getMaxShowButtonsInGroup(),
					transitionGroup.getSeq() != null ? transitionGroup.getSeq() : 0
			);
		}

	}

}
