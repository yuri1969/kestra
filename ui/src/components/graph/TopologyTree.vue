<template>
    <div>
        <div class="d-flex top rounded">
            <div>
                <b-btn-group>
                    <b-btn size="sm" @click="toggleOrientation">
                        <kicon placement="bottomright" :tooltip="$t('topology-graph.graph-orientation')">
                            <arrow-collapse-down v-if="orientation" />
                            <arrow-collapse-right v-else />
                        </kicon>
                    </b-btn>
                    <b-btn size="sm" @click="setAction('in')">
                        <kicon placement="bottom" :tooltip="$t('topology-graph.zoom-in')">
                            <magnify-plus />
                        </kicon>
                    </b-btn>
                    <b-btn size="sm" @click="setAction('out')">
                        <kicon placement="bottom" :tooltip="$t('topology-graph.zoom-out')">
                            <magnify-minus />
                        </kicon>
                    </b-btn>
                    <b-btn size="sm" @click="setAction('reset')" id="zoom-reset">
                        <kicon placement="bottom" :tooltip="$t('topology-graph.zoom-reset')">
                            <arrow-collapse-all />
                        </kicon>
                    </b-btn>
                    <b-btn size="sm" @click="setAction('fit')" id="zoom-fit">
                        <kicon placement="bottom" :tooltip="$t('topology-graph.zoom-fit')">
                            <fit-to-page />
                        </kicon>
                    </b-btn>
                </b-btn-group>
            </div>
        </div>

        <div class="graph-wrapper" :id="uuid" ref="wrapper">
            <butterfly-vue
                :canvas-data="butterflyData"
                :canvas-conf="butterflyConfs"
            />
        </div>
    </div>
</template>
<script>
    import {ButterflyVue} from "butterfly-vue";
    import "butterfly-dag/dist/index.css";
    // import Group from "./Group";
    import NodeDot from "./NodeDot";
    // import Label from "./Label";
    // import {MountingPortal} from "portal-vue"

    import TreeNode from "./TreeNode";
    import ArrowCollapseRight from "vue-material-design-icons/ArrowCollapseRight";
    import ArrowCollapseDown from "vue-material-design-icons/ArrowCollapseDown";
    import MagnifyPlus from "vue-material-design-icons/MagnifyPlus";
    import MagnifyMinus from "vue-material-design-icons/MagnifyMinus";
    import ArrowCollapseAll from "vue-material-design-icons/ArrowCollapseAll";
    import FitToPage from "vue-material-design-icons/FitToPage";
    import Utils from "../../utils/utils";
    import Kicon from "../Kicon"

    // const rightEndpoint = [
    //     {
    //         id: "right",
    //         orientation: [1, 0],
    //         pos: [0, 0.5]
    //     }
    // ];

    const endpoints = [
        {
            id: "right",
            orientation: [1, 0],
            pos: [0, 0.5]
        },
        {
            id: "left",
            orientation: [-1, 0],
            pos: [0, 0.5]
        }
    ];


    // const leftEndpoint = [
    //     {
    //         id: "left",
    //         orientation: [-1, 0],
    //         pos: [0, 0.5]
    //     }
    // ];


    export default {
        components: {
            ArrowCollapseDown,
            ArrowCollapseRight,
            MagnifyPlus,
            MagnifyMinus,
            ArrowCollapseAll,
            FitToPage,
            Kicon,
            ButterflyVue,
        },
        props: {
            flowGraph: {
                type: Object,
                required: true
            },
            flowId: {
                type: String,
                required: true
            },
            namespace: {
                type: String,
                required: true
            },
            execution: {
                type: Object,
                default: undefined
            }
        },
        data() {
            return {
                butterflyConfs: {
                    disLinkable: false,
                    linkable: false,
                    draggable: true,
                    zoomable: true,
                    moveable: true,
                    theme: {
                        group: {
                            type: "normal", // Node group type: normal (drag in and drag out), inner (can only be dragged in and not out)
                            includeGroups: true
                        },
                        edge: {
                            shapeType: "AdvancedBezier",
                            arrow: true,
                            arrowPosition: 0.5,
                            // Class: RelationEdge
                        },
                        // endpoint: {
                        //     position: [], // limit endpoint position ['Top', 'Bottom', 'Left', 'Right'],
                        //     linkableHighlight: true, // point.linkable method is triggered when connecting, can be highlighted
                        //     limitNum: 10, // limit the number of anchor connections
                        //     expandArea: {
                        //         // when the anchor point is too small, the connection hot zone can be expanded.
                        //         left: 10,
                        //         right: 10,
                        //         top: 10,
                        //         bottom: 10,
                        //     },
                        // },
                        zoomGap: 0.001, // mouse zoom in and out gap settings

                        autoResizeRootSize: true, // automatically adapt to the root size, the default is true
                    },
                    layout: {
                        type: "dagreGroupLayout",
                        options: {
                            rankdir: "LR",
                            nodesep: 40,
                            ranksep: 40,
                            controlPoints: false,
                        },
                    },
                },
                uuid: Utils.uid(),
                ready: false,
                orientation: true,
                zoom: undefined,
                zoomFactor: 1,
            };
        },
        cy: undefined,
        watch: {
            flowGraph() {
                // this.generateGraph();
            }
        },
        created() {
            this.orientation = localStorage.getItem("topology-orientation") === "1";
        },
        mounted: function () {
            // this.generateGraph();
        },
        methods: {
            forwardEvent(type, event) {
                this.$emit(type, event);
            },
            setAction(action) {
                if (action === "in") {
                    if (this.cy.zoom() <= 1.7) {
                        this.cy.zoom(this.cy.zoom() + 0.2);
                    }
                } else if (action === "out") {
                    if (this.cy.zoom() >= 0.3) {
                        this.cy.zoom(this.cy.zoom() - 0.2);
                    }
                } else if (action === "reset") {
                    this.cy.zoom(1);
                } else if (action === "fit") {
                    this.cy.fit(null, 50)
                }
            },
            toggleOrientation() {
                this.orientation = !this.orientation;
                localStorage.setItem(
                    "topology-orientation",
                    this.orientation ? 1 : 0
                );
                // this.generateGraph();
            },
            getEdgeLabel(relation) {
                let label = "";

                if (relation.relationType && relation.relationType !== "SEQUENTIAL") {
                    label = relation.relationType.toLowerCase();
                    if (relation.value) {
                        label += ` : ${relation.value}`;
                    }
                }
                return label;
            },
            getClusters() {
                const clusters = {};
                const nodes = [];

                for (let cluster of (this.flowGraph.clusters || [])) {
                    for (let nodeUid of cluster.nodes) {
                        clusters[nodeUid] = cluster.cluster;
                    }

                    nodes.push({
                        id: cluster.cluster.uid,
                        // options: {
                        //     title: cluster.cluster.task.id
                        // },
                        left: 40,
                        top: 20,
                        // name: cluster.cluster.task.id,
                        // // group: cluster.parents ? cluster.parents[cluster.parents.length - 1] : null,
                        // draggable:true,
                        // resize: true,
                        // width: 400,
                        // height: 200,
                    })
                }

                return {groups: nodes, clusters: clusters};
            },
            getNodes(clusters) {
                const nodes = [];
                // add nodes
                for (const node of this.flowGraph.nodes) {
                    const isEdge = this.isEdgeNode(node);
                    const cluster = clusters[node.uid];

                    TreeNode;
                    NodeDot;

                    const current = {
                        id: node.uid,
                        label: isEdge ? node.task.id : undefined,
                        type: isEdge ? "task" : "dot",
                        cls: node.type,
                        relationType: node.relationType,

                        name: isEdge ? node.task.id : undefined,
                        render: isEdge ? TreeNode : NodeDot,
                        endpoints: endpoints,
                        className: node.type,
                        task: node.task,

                        width: isEdge ? 202 : 5,
                        height: isEdge ? 50 : 5,

                        namespace: this.namespace,
                        flowId: this.flowId,
                        execution: this.execution
                    };

                    if (cluster) {
                        current.group = cluster.uid
                    }

                    nodes.push(current)

                    /*
                    nodes.push({
                        data: {
                            id: node.uid,
                            label: isEdge ? node.task.id : undefined,
                            type: isEdge ? "task" : "dot",
                            cls: node.type,
                            parent: cluster ? cluster.uid : undefined,
                            relationType: node.relationType
                        },
                    });
                     */
                }

                return nodes;
            },
            getEdges() {
                const edges = []
                for (const edge of this.flowGraph.edges) {
                    edges.push({
                        id: edge.source + "|" + edge.target,
                        // source: edge.source,
                        // target: edge.target,
                        label: this.getEdgeLabel(edge.relation),
                        relationType: edge.relation && edge.relation.relationType ? edge.relation.relationType : undefined,


                        sourceNode: edge.source,
                        targetNode: edge.target,
                        source: "right",
                        target: "left",
                    })
                }

                return edges;
            },
            isEdgeNode(node) {
                return node.task !== undefined && (node.type === "io.kestra.core.models.hierarchies.GraphTask" || node.type === "io.kestra.core.models.hierarchies.GraphClusterRoot")
            },
        },
        computed: {
            butterflyData() {
                // get nodes & edges
                const {groups, clusters} = this.getClusters();
                const taskNodes = this.getNodes(clusters);
                // const nodes = [...clustersNodes, ...taskNodes];
                const edges = this.getEdges();


                console.log(groups)
                return {
                    groups: groups,
                    nodes: taskNodes,
                    edges: edges
                };
            },
            treeTaskNode() {
                return this.flowGraph.nodes.filter(n => {
                    return this.isEdgeNode(n);
                })
            },
        },
        /*
        destroyed() {
            this.ready = false;
        }*/
    };
</script>
<style lang="scss" scoped>
@import "../../styles/variable";

.graph-wrapper {
    height: calc(100vh - 310px);
}

.hidden {
    opacity: 0;
    height:0;
    overflow: hidden;
}

.hide {
    opacity: 0;
}

.top {
    background-color: var(--gray-200);
    .breadcrumb {
        margin-bottom: 0;
        border-radius: 0;
        border: 0;
    }
    > div {
        margin: 6px;
    }
}
</style>
