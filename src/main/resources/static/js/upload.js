// 화면 -> 서버 , 첨부 이미지 업로드
async function uploadToServer (formObj){
    console.log(formObj)

    const response = await axios({
        method : 'post',
        url : '/upload',
        data : formObj,
        headers : {
            'Content-Type' : 'multipart/form-data'
        }
    });
    return response.data
}

// 화면 -> 서버, 첨부 이미지 삭제
async function removeFileToServer (uuid, fileName){
    console.log(uuid)
    console.log(fileName)

    const response = await axios.delete(
        //오타 수정 , delete -> remove
        `/remove/${uuid}_${fileName}`
    );
    return response.data
}