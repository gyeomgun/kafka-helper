<template>
  <div>
    <Card shadow>
      <Form ref="formCustom" :model="topicRegForm" :label-width="100" inline>
          <FormItem label="Topic Name">
              <Input type="text" v-model="topicRegForm.name"></Input>
          </FormItem>
          <FormItem label="Partition">
              <Input type="text" v-model="topicRegForm.partitionCount" number></Input>
          </FormItem>
          <FormItem label="Replication">
              <Input type="text" v-model="topicRegForm.replicationCount" number></Input>
          </FormItem>
          <FormItem>
              <Button type="primary" @click="addTopic">Add</Button>
          </FormItem>
      </Form>
    </Card>
    <Divider/>
    <Card shadow>
      <h2>Topic List</h2>
      <Table :columns="topicColumn" :data="topicData" @on-row-click="topicSelect">
        <template slot-scope="{ row, index }" slot="action">
            <Button type="primary" size="small" style="margin-right: 5px" @click="show(index)">View</Button>
            <Button type="error" size="small" @click="deleteTopic(row.name)">Delete</Button>
        </template>
      </Table>
    </Card>
    <Divider/>
    <Card shadow>
      <h2>DTO Schema</h2>
      <!-- <Tree :data="dtoSchemaTree" :style="{background: '#fff'}"></Tree> -->
    </Card>
  </div>
</template>
<script>
    export default {
        data () {                       
            return {
                topicRegForm: {
                    name: '',
                    partitionCount: 1,
                    replicationCount: 1
                },
                topicColumn: [
                    {
                        title: 'Name',
                        key: 'name'
                    },
                    {
                        title: 'partition',
                        key: 'partitionCount'
                    },
                    {
                        title: 'replication',
                        key: 'replicationCount'
                    },
                    {
                        title: 'Action',
                        slot: 'action',
                        width: 150,
                        align: 'center'
                    }
                ],
                topicData: [],
                dtoSchemaTree: [
                  {
                        title: 'parent 1',
                        expand: true,
                        children: [
                            {
                                title: 'parent 1-1',
                                expand: true,
                                children: [
                                    {
                                        title: 'leaf 1-1-1'
                                    },
                                    {
                                        title: 'leaf 1-1-2'
                                    }
                                ]
                            },
                            {
                                title: 'parent 1-2',
                                expand: true,
                                children: [
                                    {
                                        title: 'leaf 1-2-1'
                                    },
                                    {
                                        title: 'leaf 1-2-1'
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        },
        created () {
          this.getTopicList()
        },
        methods: {
            addTopic () {
              this.$api.post(`/api/topic`, this.topicRegForm)
              .then((res) => {
                console.log(res)
                if (res.data.returnCode !== '0000') {
                  this.$Notice.error({
                    title: 'Error',
                    desc: 'Could not create topic.' + res.data.returnMessage
                  });
                  return
                }
                this.getTopicList();
                this.$Notice.success({
                    title: 'Success',
                    desc: ''
                });
              })
              .catch((err) => {
                // console.log(err)
                this.$Notice.error({
                    title: 'Error',
                    desc: 'Could not create topic.' + err
                });
              })
            },
            getTopicList () {
                this.$api.get(`/api/topic`)
                .then((res) => {
                  if (res.data.returnCode !== '0000') {
                    this.$Notice.error({
                      title: 'Error',
                      desc: 'Could not retrieve topic list.' + res.data.returnMessage
                    });
                    return
                  }
                  this.topicData = res.data.info
                })
                .catch((err) => {
                  this.$Notice.error({
                    title: 'Error',
                    desc: 'Could not retrieve topic list.' + err
                });
                })
            },
            deleteTopic (name) {
              this.$api.delete(`/api/topic?name=${name}`)
              .then((res) => {
                if (res.data.returnCode !== '0000') {
                    this.$Notice.error({
                      title: 'Error',
                      desc: 'Could not delete topic' + res.data.returnMessage
                    });
                    return
                }
                this.getTopicList();
              })
              .catch((err) => {
                  this.$Notice.error({
                    title: 'Error',
                    desc: 'Could not delete topic.' + err
                });
                })
            },
            topicSelect (row) {
              console.log(row)
              // var topicName = row.name
            }
        }
    }
</script>