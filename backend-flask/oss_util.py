import oss2
import uuid
from datetime import datetime

def upload_image_to_oss(image_content):
    # 设置 OSS 的访问信息
    access_key_id = '"your_oss_access_key_id"'
    access_key_secret = 'your_oss_access_key_secret'
    bucket_name = ''
    endpoint = 'oss-cn-guangzhou.aliyuncs.com'

    # 创建 OSS 客户端
    auth = oss2.Auth(access_key_id, access_key_secret)
    bucket = oss2.Bucket(auth, endpoint, bucket_name)

    # 获取当前日期
    today_date = datetime.now().strftime('%Y/%m/%d')  # 格式化为 '年/月/日'

    # 生成唯一的文件名，不包含 '-'
    file_name = str(uuid.uuid4()).replace('-', '') + '.jpg'

    # 拼接完整的文件路径，按日期创建文件夹
    full_path = f'{today_date}/{file_name}'

    # 上传文件
    bucket.put_object(full_path, image_content)

    # 返回文件的 OSS URL
    return f'https://{bucket_name}.{endpoint}/{full_path}'
