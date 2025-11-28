import { API_BASE_URL } from "/common/config.js";

export async function fetchWithAuth(url, options = {}) {
  const accessToken = localStorage.getItem("accessToken");
  const refreshToken = localStorage.getItem("refreshToken");

  let headers = {
    ...(options.headers || {}),
    "Content-Type": "application/json",
  };

  // accessToken 자동 추가
  if (accessToken) {
    headers["Authorization"] = `Bearer ${accessToken}`;
  }

  let response = await fetch(url, {
    ...options,
    headers,
  });

  // Access Token 만료로 인해 401 발생 시 → Refresh 시도
  if (response.status === 401) {
    console.log("➡ 401 Unauthorized → Refresh Token 시도");

    const newAccessToken = await tryRefreshToken(refreshToken);

    if (newAccessToken) {
      // 새 토큰으로 Authorization 헤더 갱신
      headers["Authorization"] = `Bearer ${newAccessToken}`;

      console.log("➡ 새 Access Token으로 API 재요청");

      // API 재요청
      response = await fetch(url, {
        ...options,
        headers,
      });
    }
  }

  return response;
}

async function tryRefreshToken(refreshToken) {
  if (!refreshToken) {
    console.warn("Refresh Token 없음 — 자동 재발급 불가");
    return null;
  }

  try {
    const res = await fetch(
      `${API_BASE_URL}/api/v1/auth/refresh?refreshToken=${refreshToken}`,
      {
        method: "POST",
      }
    );

    const data = await res.json().catch(() => null);

    if (res.ok && data?.data?.accessToken) {
      const newToken = data.data.accessToken;

      // 로컬스토리지 갱신
      localStorage.setItem("accessToken", newToken);

      console.log("Access Token 자동 재발급 성공:", newToken);
      return newToken;
    } else {
      console.warn("Refresh Token 만료 또는 잘못됨 → 재발급 실패");
      return null;
    }
  } catch (err) {
    console.error("Refresh 요청 실패:", err);
    return null;
  }
}