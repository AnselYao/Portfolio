import requests, json
def get(origin, destination, mode):
    # 返回参数: distance 起点和终点的步行距离,单位: 米; duration 步行时间预计, 单位: 秒
    data = {
        'key':'56110052d12d728131b58ae1f310519f', 
        'origin':origin,
        'destination':destination,
        'output':'JSON',
        'strategy':0,
        'nightflag':0,
        'city':'长兴',
        'cityd':'长兴'
    }
    if (mode == 0) :
        # 步行
        url = "https://restapi.amap.com/v3/direction/walking"
        responses = requests.get(url,data)
        result = json.loads(responses.text)
        return result['route']['paths'][0]['distance'],result['route']['paths'][0]['duration']
    if (mode == 1) :
        # 骑行
        url = "https://restapi.amap.com/v4/direction/bicycling"
        responses = requests.get(url,data)
        result = json.loads(responses.text)
        return result['data']['paths'][0]['distance'],result['data']['paths'][0]['duration']
    if (mode == 2) :
        # 公交
        url = "https://restapi.amap.com/v3/direction/transit/integrated"
        responses = requests.get(url,data)
        result = json.loads(responses.text)
        if (result):
            if (result['route']['transits']):
                return result['route']['transits'][0]['distance'],result['route']['transits'][0]['duration']
            else:
                return 0,0
        else:
            return 0,0
        # 选择总时间最短的方案
    if (mode == 3) :
        # 驾车
        url = "https://restapi.amap.com/v3/direction/driving"
        responses = requests.get(url,data)
        result = json.loads(responses.text)
        return result['route']['paths'][0]['distance'],result['route']['paths'][0]['duration']
