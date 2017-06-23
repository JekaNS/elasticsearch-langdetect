package org.xbib.elasticsearch.plugin.langdetect;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.collect.MapBuilder;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.index.mapper.Mapper;
import org.elasticsearch.ingest.Processor;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.IngestPlugin;
import org.elasticsearch.plugins.MapperPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;
import org.xbib.elasticsearch.action.langdetect.LangdetectAction;
import org.xbib.elasticsearch.action.langdetect.TransportLangdetectAction;
import org.xbib.elasticsearch.index.mapper.langdetect.LangdetectMapper;
import org.xbib.elasticsearch.ingest.processor.LangdetectProcessor;
import org.xbib.elasticsearch.rest.action.langdetect.RestLangdetectAction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 *
 */
public class LangdetectPlugin extends Plugin implements MapperPlugin, ActionPlugin, IngestPlugin {

    @Override
    public Map<String, Mapper.TypeParser> getMappers() {
        Map<String, Mapper.TypeParser> extra = new LinkedHashMap<>();
        extra.put(LangdetectMapper.MAPPER_TYPE, new LangdetectMapper.TypeParser());
        return extra;
    }

    @Override
    public List<ActionHandler<? extends ActionRequest, ? extends ActionResponse>> getActions() {
        List<ActionHandler<? extends ActionRequest, ? extends ActionResponse>> extra = new ArrayList<>();
        extra.add(new ActionHandler<>(LangdetectAction.INSTANCE, TransportLangdetectAction.class));
        return extra;
    }

    @Override
    public List<RestHandler> getRestHandlers(Settings settings,
                                             RestController restController,
                                             ClusterSettings clusterSettings,
                                             IndexScopedSettings indexScopedSettings,
                                             SettingsFilter settingsFilter,
                                             IndexNameExpressionResolver indexNameExpressionResolver,
                                             Supplier<DiscoveryNodes> nodesInCluster) {
        List<RestHandler> extra = new ArrayList<>();
        extra.add(new RestLangdetectAction(settings, restController));
        return extra;
    }

    @Override
    public Map<String, Processor.Factory> getProcessors(Processor.Parameters parameters) {
        return MapBuilder.<String, Processor.Factory>newMapBuilder()
                .put(LangdetectProcessor.TYPE, new LangdetectProcessor.Factory())
                .immutableMap();
    }
}
