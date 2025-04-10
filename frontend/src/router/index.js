import { createRouter, createWebHistory } from 'vue-router'
import {isRoleAdmin, isUnauthorized} from "@/net";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path:'/',
            name:'welcome',
            component:() => import('@/views/WelcomeView.vue'),
            children:[
                {
                    path:'',
                    name:'welcome-login',
                    component:()=> import('@/views/welcome/LoginPage.vue')
                },{
                    path:'register',
                    name:'welcome-register',
                    component:()=> import('@/views/welcome/RegisterPage.vue')
                },{
                    path:'reset',
                    name:'welcome-reset',
                    component:() => import('@/views/welcome/ResetPage.vue')
                }
            ]
        }, {
            path:'/index',
            name:'index',
            component:()=>import('@/views/IndexView.vue'),
            children:[
                {
                    path:'',
                    name:'topics',
                    component: () => import("@/views/SchoolForum/Forum.vue"),
                    children: [
                        {
                            path: '',
                            name: 'topic-list',
                            component: () => import('@/views/SchoolForum/PostList.vue')
                        },{
                            path: 'topic-detail/:tid',
                            name: 'topic-detail',
                            component: () => import('@/views/SchoolForum/TopicDetails.vue')
                        }
                    ]
                },{
                    path:'user-setting',
                    name:'user-setting',
                    component:() => import("@/views/setting/UserSetting.vue")
                },{
                    path: 'privacy-setting',
                    name: 'privacy-setting',
                    component:() => import("@/views/setting/privacySetting.vue")
                },{
                    path:'niceBook',
                    name:'niceBook',
                    component:()=>import('@/views/niceBook/NiceBook.vue')
                }
            ]
        }, {
            path:'/admin',
            name:'admin',
            component:()=>import('@/views/AdminView.vue'),
            children:[

            ]
        }
    ]
})

router.beforeEach((to, from, next) => {
    const unauthorized = isUnauthorized(), admin = isRoleAdmin()
    if(to.name.startsWith('welcome') && !unauthorized) {
        next('/index')
    }
    else if(to.fullPath.startsWith('/admin') && !admin){
        next('/index')
    }
    else if(to.fullPath.startsWith('/index') && unauthorized) {
        next('/')
    } else {
        next()
    }
})

export default router
