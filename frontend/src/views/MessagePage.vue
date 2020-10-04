<template>
  <div>
    <Card shadow>
      <Form ref="formInline" :model="formInline" :label-width="80" inline>
        <FormItem label="Topic">
            <Select v-model="formInline.topic" placeholder="Select topic" @on-change="topicSelect">
              <Option v-for="item in topicList" :value="item.name" :key="item.name">{{ item.name }}</Option>
            </Select>
        </FormItem>
        <FormItem label="MessageId">
            <Input v-model="formInline.messageId" placeholder="Enter message id"></Input>
        </FormItem>
        <FormItem>
            <Button type="primary" @click="search(1)">Search</Button>
        </FormItem>
      </Form>
    </Card>
    <Divider/>
    <Card shadow>
      <Table :columns="messageColumn" :data="messageData">
        <template slot-scope="{ row }" slot="action">
          <Button type="success" size="small" style="margin-right: 5px" @click="show(row)">MESSAGE</Button>
        </template>
      </Table>
      <Page :total="100" show-total />
    </Card>
    <Modal v-model="messagePopup">
      <vue-json-pretty
        :data="messageJson">
      </vue-json-pretty>
    </Modal>
  </div>
</template>
<script>
import VueJsonPretty from 'vue-json-pretty'
import 'vue-json-pretty/lib/styles.css';
  export default {
    components: {
      VueJsonPretty
    },
      data () {                       
          return {
            formInline: {
              topic: '',
              service: '',
              messageId: ''
            },
            messagePopup: false,
            messageJson: '',
            selectedTopic: '',
            topicList: [],
            messageData: [],
            messageColumn: [
              {
                title: 'MessageId',
                key: 'messageId',
                fixed: 'left'
              },
              {
                title: 'Topic',
                key: 'topic'
              },
              {
                title: 'created At',
                key: 'createdAt'
              },              
              {
                title: 'Action',
                slot: 'action',
                width: 150,
                align: 'center',
                fixed: 'right'
              }
            ]
          }
      },
      created () {
        this.getTopicList()
      },
      methods: {
        topicSelect (select) {
          this.selectedTopic = select
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
            this.topicList = res.data.info
          })
          .catch((err) => {
            this.$Notice.error({
              title: 'Error',
              desc: 'Could not retrieve topic list.' + err
          });
          })
        },
        show (row) {
          this.messageJson = JSON.parse(row.messageJson)
          this.messagePopup = true
        },
        search (page) {
          this.$api.get(`/api/message?page=${page}`)
            .then((res) => {
              if (res.data.returnCode !== '0000') {
                this.$Notice.error({
                  title: 'Error',
                  desc: 'Could not retrieve message list.' + res.data.returnMessage
                });
                return
              }
              this.messageData = res.data.info
            })
            .catch((err) => {
              this.$Notice.error({
                title: 'Error',
                desc: 'Could not retrieve message list.' + err
            });
          })
        }        
      }
  }
</script>