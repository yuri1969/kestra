<template>
    <div>
        <button type="primary" icon="el-icon-edit" circle @click="labelClick"></button>
    </div>
</template>

<script>
    export default {
        props: {
            itemData: {
                type: Object,
                default: () => {}
            },
        },
        methods: {
            labelClick(e) {
                console.log("labelClick", e);
                this.$emit("dblclick", "emitlabelClick");
            }
        },
        created() {
            // console.log(this.itemData);
        }
    };
</script>