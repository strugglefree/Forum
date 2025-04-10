import {get, post} from "@/net";
import {ElMessage} from "element-plus";
import router from "@/router";
import {useStore} from "@/store";

export const apiUserInfo = (loadingRef) => {
    loadingRef.value = true
    const store = useStore()
    get('api/user/info',(data)=>{
        store.user = data
        loadingRef.value = false
    })
}

export const apiAuthRegister = (data) => {
    post("api/auth/register",data,()=>{
        ElMessage.success(`注册成功，欢迎加入这个论坛`)
        router.push('/')
    })
}

export const apiAuthAskCode = (email, type ,coldTime) => {
    coldTime.value = 60
    get(`api/auth/ask-code?email=${email}&type=${type}`,()=>{
        ElMessage.success(`验证码发送到${email}请注意查收`)
        setInterval(()=>coldTime.value--,1000);
    },(message)=>{
        ElMessage.warning(message)
        coldTime.value=0
    })
}

export const apiAuthResetConfirm = (data, activeRef) => {
    post("/api/auth/reset-confirm", data,
        () => activeRef.value++)
}

export const apiAuthResetPassword = (data) => {
    post("/api/auth/reset-password", data,
        () => {
            ElMessage.success("重置密码成功！请登录吧！")
            router.push("/")
        })
}

export const apiUserPrivacy = (success) => {
    get('/api/user/privacy',success)
}

export const apiUserPrivacySave = (data, loadingRef) => {
    loadingRef.value = false
    post("/api/user/save-privacy",data,() => {
        ElMessage.success(`隐私信息修改成功`)
        loadingRef.value = false
    })
}

export const apiUserChangePassword = (data, success) => {
    post(`/api/user/change-password`, data ,success,message => ElMessage.warning(message))
}

export const apiUserDetail = (success) => get("/api/user/details", success)

export const apiUserDetailSave = (form, success, failure) => {
    post(`api/user/save-details`, form, success, failure)
}

export const apiUserEmailReset = (emailForm , success, failure) =>
    post(`/api/user/modify-email`,emailForm,success,failure)

export const apiUserGetFollow = (data) => {
    post(`/api/follow`,data,() => {
        ElMessage.success("关注用户成功！")
        router.go(0)
    },message => ElMessage.warning("关注用户失败！" + message))
}

export const apiUserCancelFollow = (data) => {
    post(`/api/follow/cancelFollow`,data,() => {
        ElMessage.success("取关用户成功！")
        router.go(0)
    },message => ElMessage.warning("取关用户失败！" + message))
}

export const apiUserFollowList = (uid,success) =>
    get(`/api/follow/getFollowList?uid=${uid}`,success)

export const apiUserFollowInfo= (uid,success) =>
    get(`/api/follow/getFollowInfo?uid=${uid}`,success)

export const apiUserNotificationList = (success) =>
    get(`api/notification/list`,success)

export const apiUserNotificationDelete = (id,success) =>
    get(`api/notification/delete?id=${id}`,success)

export const apiUserNotificationDeleteAll = (success) =>
    get(`api/notification/delete-all`,success)