# result.py

class Result:
    def __init__(self, code=0, message="操作成功", data=None):
        self.code = code
        self.message = message
        self.data = data

    @staticmethod
    def success(data=None):
        return Result(code=0, message="操作成功", data=data)

    @staticmethod
    def error(message="操作失败"):
        return Result(code=1, message=message, data=None)

    def to_dict(self):
        return {"code": self.code, "message": self.message, "data": self.data}
