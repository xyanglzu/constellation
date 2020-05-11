/*
 * Copyright 2010-2020 Australian Signals Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package au.gov.asd.tac.constellation.views.layers.utilities;

import au.gov.asd.tac.constellation.graph.Graph;
import au.gov.asd.tac.constellation.graph.GraphConstants;
import au.gov.asd.tac.constellation.graph.GraphElementType;
import au.gov.asd.tac.constellation.graph.GraphReadMethods;
import au.gov.asd.tac.constellation.graph.manager.GraphManager;
import au.gov.asd.tac.constellation.graph.visual.contextmenu.ContextMenuProvider;
import au.gov.asd.tac.constellation.plugins.PluginExecution;
import au.gov.asd.tac.constellation.utilities.graphics.Vector3f;
import au.gov.asd.tac.constellation.views.layers.layer.LayerDescription;
import au.gov.asd.tac.constellation.views.layers.state.LayersViewState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = ContextMenuProvider.class, position = 210)
public class LayersAddContextMenu implements ContextMenuProvider {

    private static final String LAYER_MENU = "Layers";
    private static final String ADD_TO_LAYER = "Add Selection to Layer...";

    @Override
    public List<String> getMenuPath(final GraphElementType elementType) {
        return Arrays.asList(LAYER_MENU, ADD_TO_LAYER);
    }

    @Override
    public List<String> getItems(final GraphReadMethods graph, final GraphElementType elementType, final int elementId) {
        if (elementType == GraphElementType.VERTEX || elementType == GraphElementType.TRANSACTION) {
            final int stateAttributeId = LayersViewState.LayersViewConcept.MetaAttribute.LAYERS_VIEW_STATE.get(graph);
            final List<String> currentLayers = new ArrayList<>();
            if (stateAttributeId != GraphConstants.NOT_FOUND) {
                final LayersViewState currentState = graph.getObjectValue(stateAttributeId, 0);
                for (final LayerDescription layer : currentState.getAllLayers()) {
                    if (layer.getLayerIndex() > 1) {
                        currentLayers.add(String.valueOf(layer.getLayerIndex()));
                    }
                }
            } else {
                currentLayers.add("2");
            }
            return currentLayers;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void selectItem(final String item, final Graph graph, final GraphElementType elementType, final int elementId, final Vector3f unprojected) {
        PluginExecution.withPlugin(new UpdateElementBitmaskPlugin(Integer.parseInt(item), LayerAction.ADD))
                .executeLater(GraphManager.getDefault().getActiveGraph());
    }
}
