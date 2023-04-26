package dev.vcsocial.lazerwizard.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import org.eclipse.collections.api.factory.Lists;

import java.util.List;

public class LineMeshGroup implements Component {
    public static final ComponentMapper<LineMeshGroup> COMPONENT_MAPPER = ComponentMapper.getFor(LineMeshGroup.class);

    public List<LineMesh> lineMeshList = Lists.mutable.empty();
}
