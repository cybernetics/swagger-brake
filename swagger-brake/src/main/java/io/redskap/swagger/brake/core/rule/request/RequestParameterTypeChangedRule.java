package io.redskap.swagger.brake.core.rule.request;

import java.util.*;

import io.redskap.swagger.brake.core.model.Path;
import io.redskap.swagger.brake.core.model.RequestParameter;
import io.redskap.swagger.brake.core.model.Schema;
import io.redskap.swagger.brake.core.model.Specification;
import io.redskap.swagger.brake.core.rule.BreakingChangeRule;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
public class RequestParameterTypeChangedRule implements BreakingChangeRule<RequestParameterTypeChangedBreakingChange> {
    @Override
    public Collection<RequestParameterTypeChangedBreakingChange> checkRule(Specification oldApi, Specification newApi) {
        Set<RequestParameterTypeChangedBreakingChange> breakingChanges = new HashSet<>();
        for (Path path : oldApi.getPaths()) {
            Optional<Path> newApiPath = newApi.getPath(path);
            if (newApiPath.isPresent()) {
                Path newPath = newApiPath.get();
                if (CollectionUtils.isNotEmpty(path.getRequestParameters())) {
                    for (RequestParameter requestParameter : path.getRequestParameters()) {
                        Optional<RequestParameter> newRequestParameter = newPath.getRequestParameterByName(requestParameter.getName());
                        if (newRequestParameter.isPresent()) {
                            RequestParameter newRequestParam = newRequestParameter.get();
                            if (requestParameter.getSchema().isPresent()) {
                                if (newRequestParam.getSchema().isPresent()) {
                                    Schema schema = requestParameter.getSchema().get();
                                    Schema newSchema = newRequestParam.getSchema().get();
                                    Map<String, String> newTypes = newSchema.getTypes();
                                    for (Map.Entry<String, String> type : schema.getTypes().entrySet()) {
                                        String attribute = type.getKey();
                                        String typeName = type.getValue();
                                        String newType = newTypes.get(attribute);
                                        if (newType != null && !newType.equals(typeName)) {
                                            breakingChanges.add(
                                                new RequestParameterTypeChangedBreakingChange(path.getPath(), path.getMethod(),
                                                    requestParameter.getName(), attribute, typeName, newType));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return breakingChanges;
    }
}
