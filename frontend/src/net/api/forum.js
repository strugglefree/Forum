import {get, post} from "@/net";
import {ElMessage} from "element-plus";

export const apiForumGet = (success , failure) =>
    get(`api/forum/types`,success, failure)

export const apiForumGetDetail = (tid, success) =>
    get(`/api/forum/topic?tid=${tid}`,success)

export const apiForumInteract = (tid, type, topic, message) => {
    get(`/api/forum/interact?tid=${tid}&type=${type}&state=${!topic[type]}`,()=>{
        topic[type] = !topic[type]
        if(topic[type]) ElMessage.success(`${message}成功`)
        else ElMessage.success(`已取消${message}`)
    })
}

export const apiForumUpdate = (data, success, failure) =>
    post(`/api/forum/topic-update`,data,success, failure)

export const apiForumComment = (tid, page, success) =>
    get(`/api/forum/comments?tid=${tid}&page=${page}`,success)

export const apiForumDelete = (id, success) =>
    get(`api/forum/delete-comment?cid=${id}`,success)

export const apiForumTop = (success) =>
    get(`/api/forum/top-topic`, success)

export const apiForumTopicList = (page, type, success) =>
    get(`/api/forum/list-topic?page=${page}&type=${type}`, success)

export const apiForumWeather= (lon, lat, success, failure) =>
    get(`/api/forum/weather?longitude=${lon}&latitude=${lat}`,success, failure)

export const apiForumIP = (success) =>
    get(`/api/forum/get-ip`, success)

export const apiForumAddComment = (data, success) =>
    post(`/api/forum/add-comment`,data,success)

export const apiForumAddTopic = (data, success) =>
    post(`/api/forum/create-topic`,data,success)

export const apiForumCollect = (success) =>
    get(`api/forum/collect`,success)

export const apiForumCollectCancel = (tid,success) =>
    get(`/api/forum/interact?tid=${tid}&type=collect&state=false`,success)