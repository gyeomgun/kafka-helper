import Vue from 'vue'
import VueRouter from 'vue-router'
import TopicPage from '../views/TopicPage'
import ConsumerPage from '../views/ConsumerPage'
import DeadLetterPage from '../views/DeadLetterPage'
import MessagePage from '../views/MessagePage'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'TopicPage',
    component: TopicPage
  },
  {
    path: '/topic',
    name: 'TopicPage',
    component: TopicPage
  },
  {
    path: '/consumer',
    name: 'ConsumerPage',
    component: ConsumerPage
  },
  {
    path: '/deadletter',
    name: 'DeadLetterPage',
    component: DeadLetterPage
  },  
  {
    path: '/message',
    name: 'MessagePage',
    component: MessagePage
  },    
  {
    path: '/about',
    name: 'About',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/About.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
